package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.Drawing.drawDebug;
import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.Drawing.drawRobot;
import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.fromPedroPose;
import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.toPedroPose;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.opMode;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static java.lang.Double.isNaN;
import static java.lang.Math.toDegrees;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.seattlesolvers.solverslib.controller.PController;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFCoefficients;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFController;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.PIDFController;

@Configurable
public class DriveSubsystem extends HardwareSubsystem {
    public static PIDFCoefficients GOAL_LOCK_HEADING_PIDF = new PIDFCoefficients(1, 0.01, 0.175, 0);
    public static FFCoefficients GOAL_LOCK_LATERAL_FF = new FFCoefficients(0, 0, 0);
    public static boolean TEL = false;
    public static double ALLOWABLE_STILL = 1;
    public static double POWER_INTAKE = .33;
    public static double POWER_LOW = 0.50;
    public static double POWER_MEDIUM = 0.75;
    public static double POWER_HIGH = 1.00;
    public static double POWER_AUTO = 0.80;
    public static double TO_FAR = TILE_WIDTH * 3;

    public Follower follower;

    public MotorEx driveFrontLeft;
    public MotorEx driveFrontRight;
    public MotorEx driveBackLeft;
    public MotorEx driveBackRight;

    public boolean controlsReset = false;

    private final PController pForward = new PController(config.responsiveness);
    private final PController pStrafe = new PController(config.responsiveness);
    private final PController pTurn = new PController(config.responsiveness);
    private final PIDFController pidfGoalLock = new PIDFController(GOAL_LOCK_HEADING_PIDF);
    private final FFController ffGoalLock = new FFController(GOAL_LOCK_LATERAL_FF);

    private double forward = 0;
    private double strafe = 0;
    private double turn = 0;

    public DriveSubsystem() {
        follower = getFollower();
        if (follower != null)
            resetPose();

        driveFrontLeft = getMotor("driveFrontLeft", RPM_1150);
        driveFrontRight = getMotor("driveFrontRight", RPM_1150);
        driveBackLeft = getMotor("driveBackLeft", RPM_1150);
        driveBackRight = getMotor("driveBackRight", RPM_1150);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        pForward.setP(config.responsiveness);
        pStrafe.setP(config.responsiveness);
        pTurn.setP(config.responsiveness);
        pidfGoalLock.setPIDFCoefficients(GOAL_LOCK_HEADING_PIDF);

        if (opMode.isStopRequested()) {
            follower.startTeleopDrive();
            inputs(0, 0, 0);
        }

        follower.update();

        config.pose = fromPedroPose(
            follower.getPose()
        );

        drawDebug(follower);

        if (isStill() && !isBusy() && !isControlled() && vision.detectionPose != null) {
            Pose pose = toPedroPose(config.pose = vision.detectionPose);
            follower.setStartingPose(pose);
            final Style style = new Style("", "#b53fad", 0.0);
            drawRobot(pose, style);
        }

        telemetry.addData("Drive (Power)", () -> String.format("%.2f", follower.getMaxPowerScaling()));
        telemetry.addData("Drive (Controls)", () -> String.format("%.2ff, %.2fs, %.2ft", forward, strafe, turn));
        telemetry.addData("Drive (Pose)", () -> String.format("%.1fx, %.1fy, %.1f°", config.pose.x, config.pose.y, toDegrees(config.pose.heading)));
        telemetry.addData("Drive (Still)", () -> String.format("%s", isStill()));
        telemetry.addData("Drive (Busy)", () -> String.format("%s", isBusy()));

        driveFrontLeft.addTelemetry(TEL);
        driveFrontRight.addTelemetry(TEL);
        driveBackLeft.addTelemetry(TEL);
        driveBackRight.addTelemetry(TEL);
    }

    public void inputs(double forward, double strafe, double turn) {
        if (unready()) return;
        if (isBusy() && !isControlled() && !controlsReset) controlsReset = true;
        if (isBusy() && isControlled() && controlsReset) follower.startTeleopDrive();
        if (!isBusy() && !follower.isTeleopDrive()) follower.startTeleopDrive();
        if (isBusy()) return;
        follower.setTeleOpDrive(
            this.forward += pForward.calculate(this.forward, forward),
            this.strafe += pStrafe.calculate(this.strafe, strafe),
            this.turn += pTurn.calculate(this.turn, !config.goalLock ? turn : calculateGoalLockTurn()),
            config.robotCentric, config.robotCentric || isNaN(config.alliance.sign) ? 0 : config.alliance.sign *  -90
        );
    }

    public double calculateGoalLockTurn() {
        return pidfGoalLock.calculate(
            nav.getGoalHeadingError()
        ) + ffGoalLock.calculate(
            follower.getVelocity().getYComponent(),
            follower.getAcceleration().getYComponent()
        );
    }

    public boolean isStill() {
        return follower.getAcceleration().getMagnitude() < ALLOWABLE_STILL;
    }

    public boolean isBusy() {
        return follower.isBusy();
    }

    public boolean isControlled() {
        return gamepad1.getLeftX() != 0 || gamepad1.getLeftY() != 0 || gamepad1.getRightX() != 0;
    }

    public void resetPose() {
        // NOTE: When invoking setStartingPose with Pinpoint it offsets the new pose from Pinpoints
        // current pose which produces the wrong result. As a work around the follower is recreated.
        follower = getFollower();
        follower.startTeleopDrive();
        follower.setMaxPower(config.auto ? POWER_AUTO : POWER_HIGH);
        if (config.auto) config.pose = nav.getStartPose();
        follower.setStartingPose(
            toPedroPose(config.pose)
        );
    }
}
