package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Drawing;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;
import org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline;
import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public class VisionSubsystemsTests extends TestHarness {
    private static class NullLimelightVisionSubsystem extends VisionSubsystem {
        ServoEx servoMock;

        @Override
        protected <T> T getDevice(Class<? extends T> type, String deviceName, Consumer<T> consumer) {
            return null;
        }

        @Override
        protected ServoEx getServo(String id, Consumer<ServoEx> consumer) {
            if (servoMock == null)
                servoMock = mock(ServoEx.class);
            consumer.accept(servoMock);
            return servoMock;
        }
    }

    @Override
    public void setUp() {
        super.setUp();
        VisionSubsystem.POS = 0.5;
        VisionSubsystem.POS_LAST = 0.5;
        VisionSubsystem.DEG = 0;
        VisionSubsystem.PHANTOM_ANGLE = Double.NaN;
        VisionSubsystem.CAMERA_UPSIDE_DOWN = true;
        config.pose = new Pose(0, 0, 0);
        vision = new VisionSubsystem();
        vision.timer = mock(ElapsedTime.class);
    }

    @Test
    public void testConstructorAndSwitchPipelineBranches() {
        verify(vision.limelight).pipelineSwitch(Pipeline.QRCODE.index);
        verify(vision.limelight).start();

        config.auto = true;
        vision = new VisionSubsystem();
        assert VisionSubsystem.POS == 1.0;

        vision.element = new Pose(1, 2, 3);
        vision.switchPipeline(Pipeline.PURPLE, true);
        assert vision.element == null;
        verify(vision.limelight).pipelineSwitch(Pipeline.PURPLE.index);

        vision.switchPipeline(Pipeline.GREEN, false);
        verify(vision.limelight).pipelineSwitch(Pipeline.GREEN.index);

        vision = new NullLimelightVisionSubsystem();
        vision.switchPipeline(Pipeline.APRILTAG, true);
        vision = new VisionSubsystem();
        vision.timer = mock(ElapsedTime.class);
    }

    @Test
    public void testPeriodicBranchesAndTelemetrySuppliers() {
        vision.errors.add("disabled");
        vision.periodic();

        vision.errors.clear();
        clearInvocations(telemetry, vision.limelight);
        when(vision.limelight.isConnected()).thenReturn(false);
        vision.periodic();
        verify(telemetry).addData(eq("Vision"), any());
        invokeTelemetrySupplier("Vision");

        clearInvocations(telemetry, vision.limelight);
        when(vision.limelight.isConnected()).thenReturn(true);
        when(vision.limelight.getLatestResult()).thenReturn(null);
        when(vision.timer.seconds()).thenReturn(1.0);
        VisionSubsystem.POS = 0.7;
        VisionSubsystem.POS_LAST = 0.5;
        vision.periodic();
        verify(vision.timer).reset();
        verify(telemetry).addData(eq("Vision (Results)"), any());
        invokeTelemetrySupplier("Vision (Results)");

        clearInvocations(telemetry, vision.limelight);
        LLResult invalid = mock(LLResult.class);
        when(invalid.isValid()).thenReturn(false);
        when(vision.limelight.getLatestResult()).thenReturn(invalid);
        when(vision.timer.seconds()).thenReturn(1.0);
        vision.periodic();
        verify(telemetry).addData(eq("Vision (Results)"), any());
        invokeTelemetrySupplier("Vision (Results)");

        clearInvocations(telemetry, vision.limelight);
        LLResult delayed = mock(LLResult.class);
        when(delayed.isValid()).thenReturn(true);
        when(vision.limelight.getLatestResult()).thenReturn(delayed);
        when(vision.timer.seconds()).thenReturn(0.5);
        vision.periodic();
        verify(telemetry).addData(eq("Vision (Results)"), any());
        invokeTelemetrySupplier("Vision (Results)");

        clearInvocations(telemetry, vision.limelight);
        Position position = new Position(DistanceUnit.METER, 1, 2, 0, 0);
        YawPitchRollAngles angles = new YawPitchRollAngles(AngleUnit.RADIANS, 0.25, 0, 0, 0);
        LLResult aprilTag = mock(LLResult.class);
        when(aprilTag.isValid()).thenReturn(true);
        when(aprilTag.getBotpose_MT2()).thenReturn(new Pose3D(position, angles));
        when(aprilTag.getTx()).thenReturn(1.0);
        when(aprilTag.getTy()).thenReturn(2.0);
        when(aprilTag.getTxNC()).thenReturn(3.0);
        when(aprilTag.getTyNC()).thenReturn(4.0);
        when(vision.limelight.getLatestResult()).thenReturn(aprilTag);
        when(vision.timer.seconds()).thenReturn(1.0);
        vision.PIPELINE = Pipeline.APRILTAG;

        try (MockedStatic<Drawing> drawing = mockStatic(Drawing.class)) {
            vision.periodic();
            drawing.verifyNoInteractions();
        }

        assert vision.botpose != null;
        invokeTelemetrySupplier("Vision (Pipeline)");
        invokeTelemetrySupplier("Vision (Timer)");
        invokeTelemetrySupplier("Vision (Deg)");
    }

    @Test
    public void testDrawArtifactBranches() {
        try (MockedStatic<Drawing> drawing = mockStatic(Drawing.class)) {
            VisionSubsystem.PHANTOM_ANGLE = Double.NaN;
            vision.element = null;
            vision.drawArtifact();
            drawing.verifyNoInteractions();

            VisionSubsystem.PHANTOM_ANGLE = 0;
            when(TimingSubsystem.playTimer.seconds()).thenReturn(3.0);
            vision.drawArtifact();
            assert vision.element != null;
            VisionSubsystem.PHANTOM_ANGLE = 45;
            vision.drawArtifact();
            assert vision.element != null;
        }
    }

    @Test
    public void testGoalAndChaseLockBranches() {
        config.teleop = false;
        vision.goalLock(true);
        verify(vision.limelight, never()).pipelineSwitch(Pipeline.APRILTAG.index);

        config.teleop = true;
        vision.goalLock(false);
        verify(vision.limelight, never()).pipelineSwitch(Pipeline.APRILTAG.index);

        vision.goalLock(true);
        verify(vision.limelight).pipelineSwitch(Pipeline.APRILTAG.index);
        assert VisionSubsystem.POS == VisionSubsystem.POS_GOAL_LOCK;

        clearInvocations(vision.limelight);
        vision.chaseLock(false);
        verifyNoInteractions(vision.limelight);

        config.alliance = Alliance.RED;
        vision.chaseLock(true);
        verify(vision.limelight).pipelineSwitch(Pipeline.PURPLE_RIGHT.index);
        assert VisionSubsystem.POS == VisionSubsystem.POS_CHASE_LOCK;

        clearInvocations(vision.limelight);
        config.alliance = Alliance.BLUE;
        vision.chaseLock(true);
        verify(vision.limelight).pipelineSwitch(Pipeline.PURPLE_LEFT.index);
    }

    @Test
    public void testProcessQrCodeAndAprilTag() throws Exception {
        Method qrCode = VisionSubsystem.class.getDeclaredMethod("processQrCode", LLResult.class);
        qrCode.setAccessible(true);
        Method aprilTag = VisionSubsystem.class.getDeclaredMethod("processAprilTag", LLResult.class);
        aprilTag.setAccessible(true);

        LLResult qrResult = mock(LLResult.class);
        LLResultTypes.BarcodeResult barcode = mock(LLResultTypes.BarcodeResult.class);
        when(barcode.getFamily()).thenReturn("QR");
        when(barcode.getData()).thenReturn("payload");
        when(qrResult.getBarcodeResults()).thenReturn(Collections.singletonList(barcode));

        try (MockedStatic<Quanomous> quanomous = mockStatic(Quanomous.class)) {
            quanomous.when(() -> Quanomous.process("payload")).thenReturn("artifact.json");
            qrCode.invoke(vision, qrResult);
            assert "artifact.json".equals(config.quanomous);

            java.lang.reflect.Field processorsField = VisionSubsystem.class.getDeclaredField("processors");
            processorsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Pipeline, Consumer<LLResult>> processors = (Map<Pipeline, Consumer<LLResult>>) processorsField.get(vision);
            processors.get(Pipeline.QRCODE).accept(qrResult);
        }

        LLResult aprilResult = mock(LLResult.class);
        Position position = new Position(DistanceUnit.METER, 1, 2, 0, 0);
        YawPitchRollAngles angles = new YawPitchRollAngles(AngleUnit.RADIANS, 0.5, 0, 0, 0);
        when(aprilResult.getBotpose_MT2()).thenReturn(new Pose3D(position, angles));
        when(aprilResult.getTx()).thenReturn(1.0);
        when(aprilResult.getTy()).thenReturn(2.0);
        when(aprilResult.getTxNC()).thenReturn(3.0);
        when(aprilResult.getTyNC()).thenReturn(4.0);

        aprilTag.invoke(vision, aprilResult);
        assert vision.botpose != null;
        invokeTelemetrySupplier("Vision (Botpose)");
        invokeTelemetrySupplier("Vision (Tx, Ty, TxNC, TyNC)");

        java.lang.reflect.Field processorsField = VisionSubsystem.class.getDeclaredField("processors");
        processorsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Pipeline, Consumer<LLResult>> processors = (Map<Pipeline, Consumer<LLResult>>) processorsField.get(vision);
        processors.get(Pipeline.APRILTAG).accept(aprilResult);
    }

    @Test
    public void testProcessColorAndElementPoseBranches() throws Exception {
        Method processColor = VisionSubsystem.class.getDeclaredMethod("processColor", LLResult.class);
        processColor.setAccessible(true);
        Method getElementPose = VisionSubsystem.class.getDeclaredMethod("getElementPose", double.class, double.class);
        getElementPose.setAccessible(true);

        LLResult colorResult = mock(LLResult.class);
        processColor.invoke(vision, colorResult);

        config.started = true;
        when(colorResult.getColorResults()).thenReturn(Collections.emptyList());
        processColor.invoke(vision, colorResult);

        LLResultTypes.ColorResult cr = mock(LLResultTypes.ColorResult.class);
        when(cr.getTargetXDegrees()).thenReturn(20.0);
        when(cr.getTargetYDegrees()).thenReturn(30.0);
        when(colorResult.getColorResults()).thenReturn(Collections.singletonList(cr));

        config.pose = new Pose(0, 0, 0);
        processColor.invoke(vision, colorResult);
        assert vision.element != null;
        invokeTelemetrySupplier("Vision (Color Result)");
        invokeTelemetrySupplier("Vision (Element Pose)");

        VisionSubsystem.CAMERA_UPSIDE_DOWN = false;
        vision.element = null;
        config.pose = new Pose(0, -30, 0);
        when(cr.getTargetXDegrees()).thenReturn(0.0);
        when(cr.getTargetYDegrees()).thenReturn(1.0);
        processColor.invoke(vision, colorResult);
        assert vision.element == null;

        Pose elementPose = (Pose) getElementPose.invoke(vision, 10.0, 15.0);
        assert !Double.isNaN(elementPose.x);
        assert !Double.isNaN(elementPose.y);
        assert !Double.isNaN(elementPose.heading);
        invokeTelemetrySupplier("Vision (Height Diff)");
        invokeTelemetrySupplier("Vision (Elevation Angle)");
        invokeTelemetrySupplier("Vision (Bearing Angle)");
        invokeTelemetrySupplier("Vision (Element Distance)");
        invokeTelemetrySupplier("Vision (Element X Offset)");
        invokeTelemetrySupplier("Vision (Element Y Offset)");
        invokeTelemetrySupplier("Vision (Element Heading)");

        java.lang.reflect.Field processorsField = VisionSubsystem.class.getDeclaredField("processors");
        processorsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Pipeline, Consumer<LLResult>> processors = (Map<Pipeline, Consumer<LLResult>>) processorsField.get(vision);
        processors.get(Pipeline.GREEN).accept(colorResult);
        processors.get(Pipeline.PURPLE).accept(colorResult);
        processors.get(Pipeline.PURPLE_LEFT).accept(colorResult);
        processors.get(Pipeline.PURPLE_RIGHT).accept(colorResult);

        VisionSubsystem.CAMERA_UPSIDE_DOWN = true;
        when(cr.getTargetXDegrees()).thenReturn(20.0);
        when(cr.getTargetYDegrees()).thenReturn(30.0);
        Pose basePose = (Pose) getElementPose.invoke(vision, -20.0, -30.0);

        vision.element = null;
        config.pose = new Pose(0, TILE_WIDTH * 4, 0);
        processColor.invoke(vision, colorResult);
        assert vision.element == null;

        vision.element = null;
        config.pose = new Pose(-basePose.x + 0.1 * TILE_WIDTH, 0, 0);
        processColor.invoke(vision, colorResult);
        assert vision.element == null;

        vision.element = null;
        config.pose = new Pose(0, -basePose.y + 0.1 * TILE_WIDTH, 0);
        processColor.invoke(vision, colorResult);
        assert vision.element == null;
    }

    @Test
    public void testResetElement() {
        vision.element = new Pose(1, 2, 3);
        vision.resetElement();
        assert vision.element == null;
    }

    @SuppressWarnings("unchecked")
    private void invokeTelemetrySupplier(String caption) {
        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(telemetry, atLeastOnce()).addData(eq(caption), supplierCaptor.capture());
        for (Supplier supplier : supplierCaptor.getAllValues())
            supplier.get();
    }
}
