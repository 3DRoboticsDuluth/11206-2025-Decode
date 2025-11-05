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
import com.seattlesolvers.solverslib.controller.PController;
import com.seattlesolvers.solverslib.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class DriveSubsystem extends HardwareSubsystem {
    public static boolean TEL = false;
    public static double ALLOWABLE_STILL = 1;
    public static double POWER = 0;
    public static double POWER_LOW = 0.33;
    public static double POWER_MEDIUM = 0.67;
    public static double POWER_HIGH = 1.00;
    public static double TO_FAR = TILE_WIDTH * 3;

    public Follower follower;

    public MotorEx driveFrontLeft;
    public MotorEx driveFrontRight;
    public MotorEx driveBackLeft;
    public MotorEx driveBackRight;

    private final PController pForward = new PController(config.responsiveness);
    private final PController pStrafe = new PController(config.responsiveness);
    private final PController pTurn = new PController(config.responsiveness);

    private double forward = 0;
    private double strafe = 0;
    private double turn = 0;

    public DriveSubsystem() {
        POWER = POWER_MEDIUM;

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

        if (opMode.isStopRequested()) {
            follower.startTeleopDrive();
            inputs(0,0,0);
        }

        config.pose = fromPedroPose(
            follower.getPose()
        );

        follower.update();

        drawDebug(follower);

        if (isStill() && !isBusy() && !isControlled() && vision.detectionPose != null) {
            Pose pose = toPedroPose(config.pose = vision.detectionPose);
            follower.setStartingPose(pose);
            final Style style = new Style("", "#b53fad", 0.0);
            drawRobot(pose, style);
        }

        telemetry.addData("Drive (Power)", () -> String.format("%.2f", POWER));
        telemetry.addData("Drive (Controls)", () -> String.format("%.2ff, %.2fs, %.2ft", forward, strafe, turn));
        telemetry.addData("Drive (Pose)", () -> String.format("%.1fx, %.1fy, %.1f°", config.pose.x, config.pose.y, toDegrees(config.pose.heading)));
        telemetry.addData("Drive (Still)", () -> String.format("%s", isStill()));

        driveFrontLeft.addTelemetry(TEL);
        driveFrontRight.addTelemetry(TEL);
        driveBackLeft.addTelemetry(TEL);
        driveBackRight.addTelemetry(TEL);
    }

    public void inputs(double forward, double strafe, double turn) {
        if (unready()) return;
        if (isBusy() && forward + strafe + turn != 0) follower.startTeleopDrive();
        else if (isBusy()) return;
        double headingOffset = isNaN(config.alliance.sign) ? 0 : config.alliance.sign * 90;
        Vector2d driveVector = new Vector2d(forward, strafe).rotateBy(headingOffset);
        follower.setTeleOpDrive(
            this.forward += pForward.calculate(this.forward, driveVector.getX() * POWER),
            this.strafe += pStrafe.calculate(this.strafe, driveVector.getY() * POWER),
            this.turn += pTurn.calculate(this.turn, turn * POWER),
            config.robotCentric
        );
    }

    public boolean isStill() {
        return follower.getAcceleration().getMagnitude() < ALLOWABLE_STILL;
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
        follower = getFollower();
        follower.startTeleopDrive();
        follower.setStartingPose(
            toPedroPose(
                config.pose = nav.getStartPose()
            )
        );
    }
}
