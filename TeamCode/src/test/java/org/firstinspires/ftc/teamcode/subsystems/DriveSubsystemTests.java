package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.opMode;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Drawing;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.function.Supplier;

public class DriveSubsystemTests extends TestHarness {
    private static class TestDriveSubsystem extends DriveSubsystem {
        Follower followerMock;
        MotorEx frontLeft;
        MotorEx frontRight;
        MotorEx backLeft;
        MotorEx backRight;

        @Override
        protected Follower getFollower() {
            if (followerMock == null)
                followerMock = mock(Follower.class, RETURNS_DEEP_STUBS);
            return followerMock;
        }

        @Override
        protected MotorEx getMotor(String id, Motor.GoBILDA type) {
            if (frontLeft == null) frontLeft = motor();
            if (frontRight == null) frontRight = motor();
            if (backLeft == null) backLeft = motor();
            if (backRight == null) backRight = motor();

            if ("driveFrontLeft".equals(id)) return frontLeft;
            if ("driveFrontRight".equals(id)) return frontRight;
            if ("driveBackLeft".equals(id)) return backLeft;
            return backRight;
        }

        private static MotorEx motor() {
            MotorEx motor = mock(MotorEx.class);
            motor.motor = mock(DcMotor.class);
            motor.motorEx = mock(DcMotorEx.class);
            return motor;
        }
    }

    @Override
    public void setUp() {
        super.setUp();
        DriveSubsystem.follower = null;
        drive = new TestDriveSubsystem();
        config.pose = new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(0, 0, 0);
    }

    @Test
    public void testConstructorAndConfigureFollowerBranches() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        verify(subsystem.followerMock).setMaxPower(DriveSubsystem.POWER_HIGH);
        verify(subsystem.followerMock).startTeleopDrive();

        clearInvocations(subsystem.followerMock);
        config.auto = true;
        drive.configureFollower(null);
        verify(subsystem.followerMock).setMaxPower(DriveSubsystem.POWER_AUTO);
        verify(subsystem.followerMock, never()).setStartingPose(org.mockito.ArgumentMatchers.any());

        clearInvocations(subsystem.followerMock);
        org.firstinspires.ftc.teamcode.adaptations.odometry.Pose pose =
            new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(1, 2, 0.3);
        drive.configureFollower(pose);
        verify(subsystem.followerMock).setStartingPose(org.mockito.ArgumentMatchers.any(Pose.class));
    }

    @Test
    public void testPeriodicReturnsWhenUnready() {
        ((TestDriveSubsystem) drive).errors.add("disabled");
        drive.periodic();
        verify(((TestDriveSubsystem) drive).followerMock, never()).update();
    }

    @Test
    public void testPeriodicBreaksFollowingWhenStopRequested() {
        when(opMode.isStopRequested()).thenReturn(true);
        drive.periodic();
        verify(((TestDriveSubsystem) drive).followerMock).breakFollowing();
    }

    @Test
    public void testPeriodicUpdatesPoseAndTelemetry() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        when(opMode.isStopRequested()).thenReturn(false);
        when(subsystem.followerMock.getPose()).thenReturn(new Pose(1, 2, 0.5));
        when(subsystem.followerMock.getMaxPowerScaling()).thenReturn(0.75);
        when(subsystem.followerMock.getAcceleration().getMagnitude()).thenReturn(0.2);
        when(subsystem.followerMock.isBusy()).thenReturn(false);
        when(nav.getGoalHeadingRemaining()).thenReturn(0.1);
        when(nav.getGoalDistance()).thenReturn(42.0);

        try (MockedStatic<Drawing> drawing = mockStatic(Drawing.class)) {
            DriveSubsystem.CHASE_LOCK = true;
            drive.periodic();
            verify(subsystem.followerMock).update();
            drawing.verify(() -> Drawing.drawDebug(subsystem.followerMock));
            verify(subsystem.frontLeft).addTelemetry(DriveSubsystem.TEL);
            verify(subsystem.frontRight).addTelemetry(DriveSubsystem.TEL);
            verify(subsystem.backLeft).addTelemetry(DriveSubsystem.TEL);
            verify(subsystem.backRight).addTelemetry(DriveSubsystem.TEL);
            assert config.chaseLock;
            assert config.pose.x == 1;
            assert config.pose.y == 2;
            assert config.pose.heading == 0.5;
        }

        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(telemetry).addData(eq("Drive (Power)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Controls)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Pose)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Still)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Busy)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Goal Remain)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Goal Dist)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Goal Lock)"), supplierCaptor.capture());
        verify(telemetry).addData(eq("Drive (Chase Lock)"), supplierCaptor.capture());
        for (Supplier supplier : supplierCaptor.getAllValues())
            supplier.get();
    }

    @Test
    public void testInputsGuardBranches() {
        drive.inputs(1, 2, 3);
        verify(((TestDriveSubsystem) drive).followerMock, never()).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(false), anyDouble());

        config.started = true;
        ((TestDriveSubsystem) drive).errors.add("disabled");
        drive.inputs(1, 2, 3);
        verify(((TestDriveSubsystem) drive).followerMock, never()).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(false), anyDouble());
    }

    @Test
    public void testInputsBusyBranches() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        config.started = true;
        config.teleop = true;

        when(subsystem.followerMock.isBusy()).thenReturn(true);
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_x = 0.0f;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_y = 0.0f;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.right_stick_x = 0.0f;
        drive.inputs(1, 2, 3);
        assert subsystem.controlsReset;

        clearInvocations(subsystem.followerMock);
        subsystem.controlsReset = false;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_x = 1.0f;
        drive.inputs(1, 2, 3);
        verify(subsystem.followerMock, never()).startTeleopDrive();

        clearInvocations(subsystem.followerMock);
        subsystem.controlsReset = true;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_x = 1.0f;
        drive.inputs(1, 2, 3);
        verify(subsystem.followerMock).startTeleopDrive();

        clearInvocations(subsystem.followerMock);
        subsystem.controlsReset = true;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_x = 0.0f;
        drive.inputs(1, 2, 3);
        verify(subsystem.followerMock, never()).startTeleopDrive();
    }

    @Test
    public void testInputsStartsTeleopDriveWhenIdle() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        config.started = true;
        config.auto = false;
        config.teleop = true;
        config.alliance = Alliance.RED;
        config.side = Side.NORTH;
        when(subsystem.followerMock.isBusy()).thenReturn(false);
        when(subsystem.followerMock.isTeleopDrive()).thenReturn(false);
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_x = 0.0f;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_y = 0.0f;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.right_stick_x = 0.0f;
        clearInvocations(subsystem.followerMock);

        drive.inputs(1, 2, 3);

        verify(subsystem.followerMock, never()).startTeleopDrive();
        verify(subsystem.followerMock).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(false), eq(90.0));

        clearInvocations(subsystem.followerMock);
        config.auto = true;
        config.chaseLock = true;
        when(subsystem.followerMock.isTeleopDrive()).thenReturn(true);
        drive.inputs(1, 2, 3);
        verify(subsystem.followerMock).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(false), eq(0.0));

        clearInvocations(subsystem.followerMock);
        config.chaseLock = false;
        config.goalLock = true;
        config.robotCentric = false;
        config.alliance = Alliance.UNKNOWN;
        drive.inputs(1, 2, 3);
        verify(subsystem.followerMock).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(false), eq(0.0));
    }

    @Test
    public void testInputsReturnsInAutoWithoutLocks() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        config.started = true;
        config.auto = true;
        when(subsystem.followerMock.isBusy()).thenReturn(false);
        when(subsystem.followerMock.isTeleopDrive()).thenReturn(true);

        drive.inputs(1, 2, 3);

        verify(subsystem.followerMock).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(false), anyDouble());
    }

    @Test
    public void testInputsRobotCentricAndUnknownAllianceHeading() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        config.started = true;
        config.auto = false;
        config.robotCentric = true;
        config.chaseLock = false;
        config.alliance = Alliance.UNKNOWN;
        when(subsystem.followerMock.isBusy()).thenReturn(false);
        when(subsystem.followerMock.isTeleopDrive()).thenReturn(true);

        drive.inputs(1, 2, 3);

        verify(subsystem.followerMock).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(true), eq(0.0));

        clearInvocations(subsystem.followerMock);
        config.robotCentric = true;
        config.chaseLock = true;
        config.alliance = Alliance.RED;
        drive.inputs(1, 2, 3);
        verify(subsystem.followerMock).setTeleOpDrive(anyDouble(), anyDouble(), anyDouble(), eq(false), eq(0.0));
    }

    @Test
    public void testCalculateForwardBranches() {
        config.chaseLock = false;
        vision.element = new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(1, 1, 0);
        assert drive.calculateForward(3) == 3;

        config.chaseLock = true;
        vision.element = null;
        assert drive.calculateForward(4) == 4;

        vision.element = new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(1, 1, 0);
        when(nav.getArtifactForwardRemaining()).thenReturn(0.5);
        assert drive.calculateForward(5) == 5;

        when(nav.getArtifactForwardRemaining()).thenReturn(4.0);
        assert drive.calculateForward(0) != 0;
    }

    @Test
    public void testCalculateStrafeBranches() {
        config.chaseLock = false;
        vision.element = new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(1, 1, 0);
        assert drive.calculateStrafe(3) == 3;

        config.chaseLock = true;
        vision.element = null;
        assert drive.calculateStrafe(4) == 4;

        vision.element = new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(1, 1, 0);
        when(nav.getArtifactStrafeRemaining()).thenReturn(0.5);
        assert drive.calculateStrafe(5) == 5;

        when(nav.getArtifactStrafeRemaining()).thenReturn(-4.0);
        assert drive.calculateStrafe(0) != 0;
    }

    @Test
    public void testCalculateTurnBranches() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        when(subsystem.followerMock.getVelocity().getYComponent()).thenReturn(1.0);
        when(subsystem.followerMock.getAcceleration().getYComponent()).thenReturn(0.5);

        config.goalLock = false;
        config.chaseLock = false;
        vision.element = null;
        assert drive.calculateTurn(3) == 3;

        config.goalLock = true;
        when(nav.getGoalHeadingRemaining()).thenReturn(0.5);
        assert drive.calculateTurn(0) != 0;

        config.goalLock = false;
        config.chaseLock = true;
        vision.element = null;
        assert drive.calculateTurn(7) == 7;

        vision.element = new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(1, 1, 0);
        when(nav.getArtifactHeadingRemaining()).thenReturn(Math.toRadians(1));
        assert drive.calculateTurn(2) == 2;

        when(nav.getArtifactHeadingRemaining()).thenReturn(Math.toRadians(10));
        assert drive.calculateTurn(0) != 0;
    }

    @Test
    public void testStillBusyControlledAndLockBranches() {
        TestDriveSubsystem subsystem = (TestDriveSubsystem) drive;
        when(subsystem.followerMock.getAcceleration().getMagnitude()).thenReturn(0.5, 2.0);
        assert drive.isStill();
        assert !drive.isStill();

        when(subsystem.followerMock.isBusy()).thenReturn(true);
        assert drive.isBusy();

        config.teleop = false;
        assert !drive.isControlled();

        config.teleop = true;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_x = 1.0f;
        assert drive.isControlled();

        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_x = 0.0f;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_y = 1.0f;
        assert drive.isControlled();

        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.left_stick_y = 0.0f;
        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.right_stick_x = 1.0f;
        assert drive.isControlled();

        org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad.right_stick_x = 0.0f;
        assert !drive.isControlled();

        config.goalLock = true;
        DriveSubsystem.GOAL_LOCK = false;
        assert drive.getGoalLock();
        config.goalLock = false;
        DriveSubsystem.GOAL_LOCK = true;
        assert drive.getGoalLock();
        config.goalLock = false;
        DriveSubsystem.GOAL_LOCK = false;
        assert !drive.getGoalLock();

        config.chaseLock = true;
        DriveSubsystem.CHASE_LOCK = false;
        assert drive.getChaseLock();
        config.chaseLock = false;
        DriveSubsystem.CHASE_LOCK = true;
        assert drive.getChaseLock();
        config.chaseLock = false;
        DriveSubsystem.CHASE_LOCK = false;
        assert !drive.getChaseLock();
    }

    @Test
    public void testSetGoalLockBranches() {
        config.started = false;
        config.robotCentric = false;
        config.alliance = Alliance.RED;
        config.side = Side.NORTH;
        drive.setGoalLock(true);
        assert !config.goalLock;

        config.started = true;
        config.robotCentric = true;
        drive.setGoalLock(true);
        assert !config.goalLock;

        config.robotCentric = false;
        config.alliance = Alliance.UNKNOWN;
        drive.setGoalLock(true);
        assert !config.goalLock;

        config.alliance = Alliance.RED;
        config.side = Side.UNKNOWN;
        drive.setGoalLock(true);
        assert !config.goalLock;

        config.side = Side.NORTH;
        drive.setGoalLock(false);
        assert !config.goalLock;

        drive.setGoalLock(true);
        assert config.goalLock;
    }
}
