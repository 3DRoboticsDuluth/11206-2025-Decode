package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS;
import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.fromPedroPose;
import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.toPedroPose;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.hardware;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.opMode;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static java.lang.Math.toDegrees;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Drawing;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PController;

import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Constants;

@Config
public class DriveSubsystem extends SubsystemBase {
    public static boolean IMU_ENABLED = false;
    public static double ALLOWABLE_TILT = 20;
    public static double ALLOWABLE_STILL = 1;
    public static double POWER_LOW = 0.33;
    public static double POWER_MEDIUM = 0.67;
    public static double POWER_HIGH = 1.00;
    public static double TO_FAR = TILE_WIDTH * 3;

    public double power;
    public Follower follower;

    private final PController pForward = new PController(config.responsiveness);
    private final PController pStrafe = new PController(config.responsiveness);
    private final PController pTurn = new PController(config.responsiveness);

    private double forward = 0;
    private double strafe = 0;
    private double turn = 0;

    public DriveSubsystem() {
        power = POWER_MEDIUM;
//        Constants.driveConstants(Constants.class); TODO do we need this line?
        resetPose();
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (IMU_ENABLED && TimingSubsystem.periodicCount % 20 == 0)
            hardware.updateImuAnglesAndVelocities();

        pForward.setP(config.responsiveness);
        pStrafe.setP(config.responsiveness);
        pTurn.setP(config.responsiveness);

        if (isTilted() || opMode.isStopRequested()) {
            follower.startTeleopDrive();
            inputs(0,0,0);
        }

        follower.update();

        config.pose = fromPedroPose(
            follower.getPose()
        );

        follower.drawOnDashBoard();

        if (isStill() && !isBusy() && !isControlled() && vision.detectionPose != null) {
            Pose pose = toPedroPose(config.pose = vision.detectionPose);
            follower.setStartingPose(pose);
            Drawing.drawRobot(pose, "#b53fad");
        }

        telemetry.addData("Drive (Pose)", () -> String.format("%.1fx, %.1fy, %.1f°", config.pose.x, config.pose.y, toDegrees(config.pose.heading)));
        telemetry.addData("IMU (Roll)", () -> String.format("%.1f°, %.1f°/s", toDegrees(getRoll()), toDegrees(getRollRate())));
        telemetry.addData("IMU (Pitch)", () -> String.format("%.1f°, %.1f°/s", toDegrees(getPitch()), toDegrees(getPitchRate())));
        telemetry.addData("IMU (Yaw)", () -> String.format("%.1f°, %.1f°/s", toDegrees(getYaw()), toDegrees(getYawRate())));
        telemetry.addData("IMU (Tilted)", () -> String.format("%s", isTilted()));
        telemetry.addData("IMU (Still)", () -> String.format("%s", isStill()));
    }

    public void inputs(double forward, double strafe, double turn) {
        if (isTilted()) forward = strafe = turn = 0;
        if (isBusy() && forward + strafe + turn != 0) follower.startTeleopDrive();
        else if (isBusy()) return;
        follower.setTeleOpMovementVectors(
            this.forward += pForward.calculate(this.forward, forward * power),
            this.strafe += pStrafe.calculate(this.strafe, strafe * power),
            this.turn += pTurn.calculate(this.turn, turn * power),
            config.robotCentric
        );
    }

    public double getRoll() {
        return hardware.imuAngles.getRoll(RADIANS);
    }

    public double getPitch() {
        return hardware.imuAngles.getPitch(RADIANS);
    }

    public double getYaw() {
        return hardware.imuAngles.getYaw(RADIANS);
    }

    public double getRollRate() {
        return hardware.imuVelocities.xRotationRate;
    }

    public double getPitchRate() {
        return hardware.imuVelocities.yRotationRate;
    }

    public double getYawRate() {
        return hardware.imuVelocities.zRotationRate;
    }

    public boolean isTilted() {
        return Math.abs(getRoll()) + Math.abs(getPitch()) >= Math.toRadians(ALLOWABLE_TILT);
    }

    public boolean isStill() {
        return Math.abs(getRollRate()) + Math.abs(getPitchRate() + Math.abs(getYawRate())) <= Math.toRadians(ALLOWABLE_STILL);
    }

    public boolean isBusy() {
        return follower.isBusy();
    }

    public boolean isControlled() {
        return gamepad1.getLeftY() != 0 ||
            gamepad1.getLeftX() != 0 ||
            gamepad1.getRightX() != 0;
    }

    public void resetPose() {
        // NOTE: When invoking setStartingPose with Pinpoint it offsets the new pose from Pinpoints
        // current pose which produces the wrong result. As a work around the follower is recreated.
        follower = Constants.createFollower(hardwareMap);
        follower.startTeleopDrive();
        follower.setStartingPose(
            toPedroPose(
                config.pose = nav.getStartPose()
            )
        );
    }
}
