package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.COLOR;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.QRCODE;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.APRILTAG;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static java.lang.Math.toDegrees;

import android.annotation.SuppressLint;
import android.util.Log;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class VisionSubsystem extends HardwareSubsystem {
    public static boolean TEL = false;
    public static boolean CAMERA_UPSIDE_DOWN = true;
    public static int PIPELINE = 0;

    public Pose detectionPose = null;
    public int detectionCount = 0;
    public Pose elementPose = null;

    public String qrCodeData = null;
    public boolean qrScanRequested = false;

    public static double MAX = .175;
    public static double MIN = 0;
    public static double POS = 0;

    public final Limelight3A limelight;

    public final Servo servo;

    Map<Integer, Consumer<LLResult>> processors;

    public VisionSubsystem() {
        limelight = getDevice(
            Limelight3A.class,
            "limelight",
            l -> {
                switchPipeline(PIPELINE, true);
                l.start();
            }
        );

        servo = getServo("turret");

        processors = new HashMap<Integer, Consumer<LLResult>>() {{
            put(QRCODE.index, VisionSubsystem.this::processQRCode);
            put(APRILTAG.index, VisionSubsystem.this::processAprilTag);
            put(COLOR.index, VisionSubsystem.this::processColor);
        }};

    }

    /** @noinspection DataFlowIssue*/
    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        if (!limelight.isConnected()) {
            telemetry.addData("Vision", () -> "Connection Issue!");
            return;
        }

        if (qrScanRequested && qrCodeData == null) {
            switchPipeline(QRCODE.index, false);
        } else if (!qrScanRequested) {
            switchPipeline(PIPELINE, false);
        }

        detectionPose = null;

        double yaw = toDegrees(config.pose.heading);
        limelight.updateRobotOrientation(yaw);

        LLResult result = limelight.getLatestResult();

        POS = clamp(POS, MIN, MAX);
        servo.addTelemetry(TEL);

        if (result == null || !result.isValid()) {
            telemetry.addData("Vision", () -> "No data available");
            return;
        }

        processors.get(PIPELINE).accept(result);
    }

    public void switchPipeline(int pipeline, boolean elementReset) {
        if (limelight == null) return;
        if (elementReset) elementPose = null;
        limelight.pipelineSwitch(PIPELINE = pipeline);
    }

    @SuppressLint("DefaultLocale")
    private void processAprilTag(LLResult result) {
        Pose3D botpose = result.getBotpose_MT2();

        detectionPose = new Pose(
             botpose.getPosition().x,
             botpose.getPosition().y,
             botpose.getOrientation().getYaw(AngleUnit.RADIANS)
        );

        telemetry.addData("Vision (Detection Count)", () -> String.format("%d", ++detectionCount));

        Log.i(this.getClass().getSimpleName(), String.format("%d", ++detectionCount));

        telemetry.addData(
            "Vision (Detection Pose)",
            () -> String.format(
                "%.1fx, %.1fy, %.1f°",
                detectionPose.x,
                detectionPose.y,
                toDegrees(detectionPose.heading)
            )
        );

        Log.i(
            this.getClass().getSimpleName(),
            String.format(
                "Vision (Detection Pose) | %.1fx, %.1fy, %.1f°",
                detectionPose.x,
                detectionPose.y,
                toDegrees(detectionPose.heading)
            )
        );
    }

    @SuppressLint("DefaultLocale")
    private void processColor(LLResult result) {
        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();

        for (LLResultTypes.ColorResult cr : colorResults) {
            double direction = CAMERA_UPSIDE_DOWN ? -1 : 1;

            telemetry.addData(
                 "Vision (Color Result)",
                 () -> String.format(
                     "%.2f°tx, %.2f°ty",
                     direction * cr.getTargetXDegrees(),
                     direction * cr.getTargetYDegrees()
                 )
            );

            Log.i(
                this.getClass().getSimpleName(),
                String.format(
                    "Vision (Color Result) | %.2f°tx, %.2f°ty",
                    direction * cr.getTargetXDegrees(),
                    direction * cr.getTargetYDegrees()
                )
            );

            telemetry.addData(
                "Vision (Element Pose)",
                () -> String.format(
                    "%.1fx, %.1fy, %.1f°",
                    elementPose.x,
                    elementPose.y,
                    toDegrees(elementPose.heading)
                )
            );

            Log.i(
                this.getClass().getSimpleName(),
                String.format(
                    "Vision (Element Pose) | %.1fx, %.1fy, %.1f°",
                    elementPose.x,
                    elementPose.y,
                    toDegrees(elementPose.heading)
                )
            );

            return;
        }
    }

    @SuppressLint("DefaultLocale")
    private void processQRCode(LLResult result) {
        List<LLResultTypes.BarcodeResult> barcodeResults = result.getBarcodeResults();

        for(LLResultTypes.BarcodeResult qrResult : barcodeResults) {

            Log.i(
                this.getClass().getSimpleName(),
                String.format("QR Code | Family: %s |Data: %s", qrResult.getFamily(), qrResult.getData())
            );

            config.quanomous = Quanomous.process(qrResult.getData());
        }
    }

    public void startQrScan() {
        qrScanRequested = true;
    }

    public void stopQrScan() {
        qrScanRequested = false;
    }
}
