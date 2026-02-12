package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.util.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.Drawing.drawDebug;
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
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static java.lang.Math.toDegrees;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.seattlesolvers.solverslib.controller.PController;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFCoefficients;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFController;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.PIDFController;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;

@Configurable
public class DriveSubsystem extends HardwareSubsystem {
    public static PIDFCoefficients FORWARD_PIDF = new PIDFCoefficients(0.025, 0.005, 0.005, 0.05);
    public static PIDFCoefficients STRAFE_PIDF = new PIDFCoefficients(0.025, 0.005, 0.005, 0.05);
    public static PIDFCoefficients HEADING_PIDF = new PIDFCoefficients(0.5, 0.005, 0.05, 0.05);
    public static FFCoefficients HEADING_FF = new FFCoefficients(0, 0, 0);
    public static boolean TEL = false;
    public static double ALLOWABLE_STILL = 1;
    public static double POWER_INTAKE = 0.5;
    public static double POWER_LOW = 0.50;
    public static double POWER_MEDIUM = 0.75;
    public static double POWER_HIGH = 1.00;
    public static double POWER_AUTO = 0.8;
    public static double TO_FAR = TILE_WIDTH * 3;
    public static double GOAL_LOCK_MAX_TURN = 0.4;
    public static boolean GOAL_LOCK = false;
    public static boolean CHASE_LOCK = false;

    public static Follower follower;

    public MotorEx driveFrontLeft;
    public MotorEx driveFrontRight;
    public MotorEx driveBackLeft;
    public MotorEx driveBackRight;

    public boolean controlsReset = false;

    private final PController pForward = new PController(config.responsiveness);
    private final PController pStrafe = new PController(config.responsiveness);
    private final PController pTurn = new PController(config.responsiveness);
    private final PIDFController pidfForward = new PIDFController(FORWARD_PIDF);
    private final PIDFController pidfStrafe = new PIDFController(STRAFE_PIDF);
    private final PIDFController pidfTurn = new PIDFController(HEADING_PIDF);
    private final FFController ffTurn = new FFController(HEADING_FF);

    private double forward = 0;
    private double strafe = 0;
    private double turn = 0;

    public DriveSubsystem() {
        this.setGoalLock(false);
        this.setChaseLock(false);
        this.configureFollower(null);
        driveFrontLeft = getMotor("driveFrontLeft", RPM_1150);
        driveFrontRight = getMotor("driveFrontRight", RPM_1150);
        driveBackLeft = getMotor("driveBackLeft", RPM_1150);
        driveBackRight = getMotor("driveBackRight", RPM_1150);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        if (CHASE_LOCK) config.chaseLock = true;

        pForward.setP(config.responsiveness);
        pStrafe.setP(config.responsiveness);
        pTurn.setP(config.responsiveness);
        pidfForward.setPIDFCoefficients(FORWARD_PIDF);
        pidfStrafe.setPIDFCoefficients(STRAFE_PIDF);
        pidfTurn.setPIDFCoefficients(HEADING_PIDF);

        if (opMode.isStopRequested()) {
            follower.breakFollowing();
            return;
        }

        follower.update();

        config.pose = fromPedroPose(
            follower.getPose()
        );

        drawDebug(follower);

        telemetry.addData("Drive (Power)", () -> String.format("%.2f", follower.getMaxPowerScaling()));
        telemetry.addData("Drive (Controls)", () -> String.format("%.2ff, %.2fs, %.2ft", forward, strafe, turn));
        telemetry.addData("Drive (Pose)", () -> String.format("%.1fx, %.1fy, %.1f°", config.pose.x, config.pose.y, toDegrees(config.pose.heading)));
        telemetry.addData("Drive (Still)", () -> String.format("%s", isStill()));
        telemetry.addData("Drive (Busy)", () -> String.format("%s", isBusy()));
        telemetry.addData("Drive (Goal Remain)", () -> String.format("%.1f", toDegrees(nav.getGoalHeadingRemaining())));
        telemetry.addData("Drive (Goal Dist)", () -> String.format("%.1f", nav.getGoalDistance()));
        telemetry.addData("Drive (Goal Lock)", () -> String.format("%s", this.getGoalLock()));
        telemetry.addData("Drive (Chase Lock)", () -> String.format("%s", this.getChaseLock()));

        driveFrontLeft.addTelemetry(TEL);
        driveFrontRight.addTelemetry(TEL);
        driveBackLeft.addTelemetry(TEL);
        driveBackRight.addTelemetry(TEL);
    }

    public void inputs(double forward, double strafe, double turn) {
        if (unready() || !config.started) return;
        if (isBusy() && !isControlled() && !controlsReset) controlsReset = true;
        if (isBusy() && isControlled() && controlsReset) follower.startTeleopDrive();
        if (!isBusy() && !follower.isTeleopDrive()) follower.startTeleopDrive();
        if (isBusy() || (config.auto && !this.getChaseLock() && !this.getGoalLock())) return;
        follower.setTeleOpDrive(
            this.forward += pForward.calculate(this.forward, calculateForward(forward)),
            this.strafe += pStrafe.calculate(this.strafe, calculateStrafe(strafe)),
            this.turn += pTurn.calculate(this.turn, calculateTurn(turn)),
            config.robotCentric  && !this.getChaseLock(),
            config.robotCentric || config.chaseLock || isNaN(config.alliance.sign) ? 0 : config.alliance.sign *  -90
        );
    }

    public double calculateForward(double forward) {
        if (!this.getChaseLock() || vision.element == null) return forward;
        double remaining = nav.getArtifactForwardRemaining();
        if (abs(remaining) < 1) return forward;
        return pidfForward.calculate(remaining) - signum(remaining) * pidfForward.getF();
    }

    public double calculateStrafe(double strafe) {
        if (!this.getChaseLock() || vision.element == null) return strafe;
        double remaining = nav.getArtifactStrafeRemaining();
        if (abs(remaining) < 1) return strafe;
        return pidfStrafe.calculate(remaining) - signum(remaining) * pidfStrafe.getF();
    }

    public double calculateTurn(double turn) {
        double remaining;

        if (this.getGoalLock()) remaining = nav.getGoalHeadingRemaining();
        else if (this.getChaseLock() && vision.element != null) remaining = nav.getArtifactHeadingRemaining();
        else return turn;

        if (this.getChaseLock() && abs(toDegrees(remaining)) < 2) return turn;

        return (
            clamp(pidfTurn.calculate(remaining) - signum(remaining) * pidfTurn.getF(), -GOAL_LOCK_MAX_TURN, GOAL_LOCK_MAX_TURN)
        ) + ffTurn.calculate(
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
        return config.teleop && (
            gamepad1.getLeftX() != 0 ||
            gamepad1.getLeftY() != 0 ||
            gamepad1.getRightX() != 0
        );
    }

    public boolean getGoalLock() {
        return config.goalLock || GOAL_LOCK;
    }

    public void setGoalLock(boolean enabled) {
        config.goalLock = GOAL_LOCK =
            config.started && !config.robotCentric &&
                config.alliance != Alliance.UNKNOWN &&
                config.side != Side.UNKNOWN && enabled;
    }

    public boolean getChaseLock() {
        return config.chaseLock || CHASE_LOCK;
    }

    public void setChaseLock(boolean enabled) {
        config.chaseLock = CHASE_LOCK = enabled;
    }

    public void configureFollower(Pose pose) {
        if (follower == null || pose != null)
            follower = getFollower();
        follower.setMaxPower(config.auto ? POWER_AUTO : POWER_HIGH);
        if (pose != null)
            follower.setStartingPose(toPedroPose(pose));
        follower.startTeleopDrive();
    }
}
