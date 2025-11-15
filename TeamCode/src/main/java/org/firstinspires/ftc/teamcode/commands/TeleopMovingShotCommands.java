package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.toPedroPose;
import static org.firstinspires.ftc.teamcode.commands.Commands.*;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.*;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;
import static java.lang.Math.*;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.math.Vector;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.adaptations.balistics.MovingShooterModel;
import org.firstinspires.ftc.teamcode.adaptations.balistics.MovingShooterModel.ShotParameters;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;

/**
 * Commands for shooting while driver is actively controlling the robot.
 * Works in TELEOP with manual joystick control!
 */
@Configurable
public class TeleopMovingShotCommands {

    // Control modes
    public enum AimMode {
        MANUAL,          // Driver has full control
        DRIVER_ASSIST,   // System adjusts heading while driver controls movement
        AUTO_AIM,        // System controls heading, driver controls speed/direction
        FULL_AUTO        // System takes full control momentarily
    }

    public enum DepositTarget {
        NEAR,
        FAR
    }
    public static AimMode CURRENT_MODE = AimMode.AUTO_AIM;
    public static DepositTarget CURRENT_DEPOSIT = DepositTarget.NEAR;

    // Assist parameters
    public static double HEADING_ASSIST_STRENGTH = 0.7;  // 0=off, 1=full
    public static double MIN_SPEED_FOR_SHOT = 2.0;       // in/s
    public static double MAX_SPEED_FOR_SHOT = 50.0;      // in/s
    // Shot readiness thresholds
    public static double HEADING_TOLERANCE = 3.0;           // degrees (reduced from 8.0)
    public static double ANGULAR_VELOCITY_TOLERANCE = 3.0;  // deg/s - must not be spinning
    public static double POSITION_VELOCITY_TOLERANCE = 3.0; // in/s - for FULL_AUTO stability
    private final static double doherty = 0.4;
    public static double READY_HOLD_TIME = doherty * 2;    // seconds - must be ready this long
    // Continuous tracking - made public for telemetry access
    public ShotParameters currentParams = null;
    public boolean shotReady = false;
    private long shotReadyStartTime = 0;
    private boolean wasReadyLastLoop = false;
    public static double HEADING_ASSIST_GAIN = 0.8;      // Reduced from 2.0
    public static double AUTO_AIM_GAIN = 1.5;            // Reduced from 3.0
    public static double HEADING_DEADBAND = 2.0;         // Don't correct tiny errors (degrees)
    private long lastCalcTime = 0;
    private double lastDriverForward = 0;
    private double lastDriverStrafe = 0;
    private double lastDriverTurn = 0;

    private void enableGoalLock() {
        Commands.drive.goalLock(true);
    }

    private void disableGoalLock() {
        Commands.drive.goalLock(false);
    }

    /**
     * MAIN COMMAND: Driver-controlled shooting while moving.
     * Runs continuously during teleop.
     */
    public Command driverControlledShoot() {
        return new Command() {
            @Override
            public void initialize() {
                FlywheelSubsystem.VEL = FlywheelSubsystem.FWD;
            }

            @Override
            public void execute() {
                // Driver inputs
                double forward = -gamepad1.gamepad.left_stick_y;
                double strafe = -gamepad1.gamepad.left_stick_x;
                double turn = -gamepad1.gamepad.right_stick_x;

                lastDriverForward = forward;
                lastDriverStrafe = strafe;
                lastDriverTurn = turn;

                // Actual robot velocity
                Vector velocity = drive.follower.getVelocity();
                double vx = velocity.getXComponent();
                double vy = velocity.getYComponent();
                double speed = velocity.getMagnitude();

                // Calculate shot parameters
                Pose targetPose = nav.getGoalPose();
                currentParams = MovingShooterModel.calculateMovingShot(
                        config.pose, targetPose, vx, vy
                );

                // Update deflector
                if (currentParams != null && currentParams.isValid()) {
                    DeflectorSubsystem.AUTO_MODE = false;
                    DeflectorSubsystem.POS = DeflectorSubsystem.angleToPosition(
                            currentParams.deflectorAngleDeg
                    );
                }

                // Check shot readiness
                boolean flywheelReady = FlywheelSubsystem.VEL > 0 &&
                        flywheel.motorLeft.getVelocityPercentage() >= FlywheelSubsystem.VEL * FlywheelSubsystem.THRESH &&
                        flywheel.motorRight.getVelocityPercentage() >= FlywheelSubsystem.VEL * FlywheelSubsystem.THRESH;

                shotReady = currentParams != null &&
                        currentParams.isValid() &&
                        speed >= MIN_SPEED_FOR_SHOT &&
                        speed <= MAX_SPEED_FOR_SHOT &&
                        flywheelReady &&
                        abs(currentParams.headingAdjustDeg) < HEADING_TOLERANCE;

                shotReady = currentParams != null &&
                        currentParams.isValid() &&
                        speed >= MIN_SPEED_FOR_SHOT &&
                        speed <= MAX_SPEED_FOR_SHOT &&
                        flywheelReady &&
                        abs(currentParams.headingAdjustDeg) < HEADING_TOLERANCE &&
                        abs(drive.follower.getVelocity().getTheta()) < toRadians(ANGULAR_VELOCITY_TOLERANCE);

                if (shotReady && !wasReadyLastLoop) {
                    // Just became ready - start timer
                    shotReadyStartTime = System.currentTimeMillis();
                    wasReadyLastLoop = true;
                } else if (!shotReady) {
                    // Lost ready state - reset
                    wasReadyLastLoop = false;
                    shotReadyStartTime = 0;
                }

                boolean steadyShot = shotReady &&
                        (System.currentTimeMillis() - shotReadyStartTime) >= (READY_HOLD_TIME * 1000);

                // Apply control mode
                switch (CURRENT_MODE) {
                    case MANUAL:
                        disableGoalLock();
                        drive.inputs(forward, strafe, turn);
                        break;

                    case DRIVER_ASSIST:
                        disableGoalLock();
                        double assistedTurn = applyHeadingAssist(turn);
                        drive.inputs(forward, strafe, assistedTurn);
                        break;

                    case AUTO_AIM:
                        enableGoalLock();
                        drive.inputs(forward, strafe, 0);
                        break;

                    case FULL_AUTO:
                        if (!drive.follower.isBusy()) {
                            Pose depositPose = (CURRENT_DEPOSIT == DepositTarget.NEAR)
                                    ? nav.getLaunchNearPose()
                                    : nav.getLaunchFarPose();

                            drive.follower.followPath(
                                    drive.follower.pathBuilder()
                                            .addPath(new com.pedropathing.geometry.BezierLine(
                                                    () -> toPedroPose(config.pose),
                                                    toPedroPose(depositPose)
                                            ))
                                            .setLinearHeadingInterpolation(
                                                    config.pose.heading,
                                                    depositPose.heading
                                            )
                                            .build()
                            );
                        }

                        if (shotReady && drive.follower.atParametricEnd()) {
                            fireWhenReady().schedule();

                            CURRENT_MODE = AimMode.AUTO_AIM;

                            gamepad1.gamepad.rumble(1.0, 1.0, 800);

                            drive.follower.breakFollowing();
                        }
                        break;
                }

                if (steadyShot && CURRENT_MODE != AimMode.FULL_AUTO) {
                    gamepad1.gamepad.rumble(0.3, 0.3, 50);
                }

                lastCalcTime = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void end(boolean interrupted) {
                FlywheelSubsystem.VEL = FlywheelSubsystem.STOP;
                disableGoalLock();
                drive.follower.breakFollowing();
                drive.follower.startTeleopDrive();

                DeflectorSubsystem.AUTO_MODE = true;
            }

            @Override
            public java.util.Set<com.seattlesolvers.solverslib.command.Subsystem> getRequirements() {
                return new java.util.HashSet<>();
            }
        };
    }

    public Command cycleDepositTarget() {
        return new InstantCommand(() -> {
            switch (CURRENT_DEPOSIT) {
                case NEAR:
                    CURRENT_DEPOSIT = DepositTarget.FAR;
                    gamepad1.gamepad.rumble(0.5, 0.5, 300); // Long rumble = FAR
                    break;
                case FAR:
                    CURRENT_DEPOSIT = DepositTarget.NEAR;
                    gamepad1.gamepad.rumble(0.5, 0.5, 150); // Short rumble = NEAR
                    break;
            }
        });
    }

    // --- Helper methods ---
    private double applyHeadingAssist(double driverTurn) {
        if (currentParams == null || !currentParams.isValid()) return driverTurn;

        // ✅ Add deadband to prevent micro-corrections
        if (abs(currentParams.headingAdjustDeg) < HEADING_DEADBAND) {
            return driverTurn;
        }

        double requiredTurn = toRadians(currentParams.headingAdjustDeg) * HEADING_ASSIST_GAIN;
        double assistScale = HEADING_ASSIST_STRENGTH * (1.0 - abs(driverTurn));
        return driverTurn + requiredTurn * assistScale;
    }

    private double calculateAutoTurn() {
        if (currentParams == null || !currentParams.isValid()) return 0;

        // ✅ Add deadband
        if (abs(currentParams.headingAdjustDeg) < HEADING_DEADBAND) {
            return 0;
        }

        double error = toRadians(currentParams.headingAdjustDeg);
        return clamp(error * AUTO_AIM_GAIN, -0.8, 0.8);
    }

    public Command fireWhenReady() {
        return new InstantCommand(() -> {
            if (shotReady) {
//                GateSubsystem.POS = GateSubsystem.OPEN;
                new Command() {
                    private long startTime = 0;

                    @Override
                    public void initialize() { startTime = System.currentTimeMillis(); ConveyorSubsystem.VEL = ConveyorSubsystem.LAUNCH; }

                    @Override
                    public boolean isFinished() { return (System.currentTimeMillis() - startTime) > 1000; }

                    @Override
                    public void end(boolean interrupted) {
//                        GateSubsystem.POS = GateSubsystem.CLOSE;
                        ConveyorSubsystem.VEL = ConveyorSubsystem.STOP;
                    }

                    @Override
                    public java.util.Set<com.seattlesolvers.solverslib.command.Subsystem> getRequirements() { return new java.util.HashSet<>(); }
                }.schedule();
            }
        });
    }

    public Command cycleAimMode() {
        return new InstantCommand(() -> {
            switch (CURRENT_MODE) {
                case MANUAL: CURRENT_MODE = AimMode.DRIVER_ASSIST; gamepad1.gamepad.rumble(0.5,0.5,200); break;
                case DRIVER_ASSIST: CURRENT_MODE = AimMode.AUTO_AIM; gamepad1.gamepad.rumble(0.5,0.5,400); break;
                case AUTO_AIM: CURRENT_MODE = AimMode.FULL_AUTO; gamepad1.gamepad.rumble(0.5,0.5,600); break;
                case FULL_AUTO: CURRENT_MODE = AimMode.MANUAL; gamepad1.gamepad.rumble(0.5,0.5,100); break;
            }
        });
    }

    public Command quickShot() {
        return new InstantCommand(() -> {
            if (currentParams != null && currentParams.isValid()) {
                // Lock on the goal while shooting
                enableGoalLock();

                // Set deflector
                double position = DeflectorSubsystem.angleToPosition(currentParams.deflectorAngleDeg);
                DeflectorSubsystem.POS = position;

                // Open gate and launch conveyor
                GateSubsystem.POS = GateSubsystem.OPEN;
                ConveyorSubsystem.VEL = ConveyorSubsystem.LAUNCH;

                // Schedule the cleanup sequence
                new Command() {
                    private long startTime = 0;

                    @Override
                    public void initialize() {
                        startTime = System.currentTimeMillis();
                    }

                    @Override
                    public void execute() {
                        // Nothing else needed here; just wait
                    }

                    @Override
                    public boolean isFinished() {
                        return (System.currentTimeMillis() - startTime) > 300; // 0.3s
                    }

                    @Override
                    public void end(boolean interrupted) {
                        GateSubsystem.POS = GateSubsystem.CLOSE;
                        ConveyorSubsystem.VEL = ConveyorSubsystem.STOP;
                        disableGoalLock();
                    }

                    @Override
                    public java.util.Set<com.seattlesolvers.solverslib.command.Subsystem> getRequirements() {
                        return new java.util.HashSet<>();
                    }
                }.schedule();
            }
        });
    }


    public boolean isShotReady() { return shotReady; }

    public AimMode getCurrentMode() { return CURRENT_MODE; }

    public double getShotQuality() {
        if (currentParams == null || !currentParams.isValid()) return 0;
        Vector velocity = drive.follower.getVelocity();
        double speed = velocity.getMagnitude();
        boolean flywheelReady = FlywheelSubsystem.VEL > 0 &&
                flywheel.motorLeft.getVelocityPercentage() >= FlywheelSubsystem.VEL * FlywheelSubsystem.THRESH &&
                flywheel.motorRight.getVelocityPercentage() >= FlywheelSubsystem.VEL * FlywheelSubsystem.THRESH;

        double headingQuality = 1.0 - min(1.0, abs(currentParams.headingAdjustDeg) / 20.0);
        double speedQuality = (speed >= MIN_SPEED_FOR_SHOT && speed <= MAX_SPEED_FOR_SHOT) ? 1.0 : 0.5;
        double flywheelQuality = flywheelReady ? 1.0 : 0.3;

        return (headingQuality + speedQuality + flywheelQuality) / 3.0;
    }

    public Command emergencyStop() {
        return new InstantCommand(() -> {
            CURRENT_MODE = AimMode.MANUAL;
            FlywheelSubsystem.VEL = FlywheelSubsystem.STOP;
            ConveyorSubsystem.VEL = ConveyorSubsystem.STOP;
            GateSubsystem.POS = GateSubsystem.CLOSE;
            disableGoalLock();
        });
    }

    private double clamp(double value, double min, double max) { return Math.max(min, Math.min(max, value)); }
}
