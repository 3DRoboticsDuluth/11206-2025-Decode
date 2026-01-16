package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.BARE;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Double.isNaN;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.adaptations.ballistics.BallisticsModel;
import org.firstinspires.ftc.teamcode.adaptations.ballistics.FlywheelController;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFCoefficients;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFController;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class FlywheelSubsystem extends HardwareSubsystem {
    public static FFCoefficients FF = new FFCoefficients(0.00007, 0.000185, 0);
    public static PIDFCoefficients PIDF = new PIDFCoefficients(0.00025, 0, 0, 0);
    public static FFCoefficients AXIAL_FF = new FFCoefficients(0, 0, 0);
    public static double DIST_MIN = 24;
    public static double RPM_MAX = 6000;
    public static double FWD = 0.4 * RPM_MAX;
    public static double REV = -0.5 * RPM_MAX;
    public static double HOLD = -0.2 * RPM_MAX;
    public static double STOP = 0;
    public static double THRESH = 0.98;
    public static double VEL = STOP;
    public static boolean TEL = false;

    public VoltageSensor voltageSensor;
    public MotorEx motorLeft;
    public MotorEx motorRight;

    private final FlywheelController controllerLeft = new FlywheelController(FF, PIDF);
    private final FlywheelController controllerRight = new FlywheelController(FF, PIDF);
    public static FFController controllerAxial = new FFController(AXIAL_FF);

    public FlywheelSubsystem() {
        voltageSensor = getDevice(VoltageSensor.class, "Control Hub");
        motorLeft = getMotor("flywheelLeft", BARE, m -> configure(m, true));
        motorRight = getMotor("flywheelRight", BARE, m -> configure(m, false));
        VEL = STOP;
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        VEL = calculateVelocity();

        set(motorLeft, controllerLeft);
        set(motorRight, controllerRight);
    }

    public void forward() {
        VEL = FWD;
    }

    public void stop() {
        VEL = STOP;
    }

    public void reverse() {
        VEL = REV;
    }

    public void hold() {
        VEL = HOLD;
    }

    public boolean isReady() {
        VEL = calculateVelocity();
        return motorLeft.getRpm() >= VEL * THRESH &&
            motorRight.getRpm() >= VEL * THRESH;
    }

    private void configure(MotorEx motor, boolean inverted) {
        motor.setZeroPowerBehavior(FLOAT);
        motor.setInverted(inverted);
    }

    private void set(MotorEx motor, FlywheelController controller) {
        controller.setCoefficients(FF, PIDF);
        controller.setTarget(VEL);
        motor.set(controller.update(motor.getRpm()));
        motor.addTelemetry(TEL);
    }

    private double calculateVelocity() {
        double velocity = VEL;

        if (config.started && (config.goalLock || config.robotCentric))
            velocity = BallisticsModel.flywheelRpm(
                nav.getGoalDistance() + (
                    config.pose.x < TILE_WIDTH ?
                        config.goalDistanceOffsetSouth :
                        config.goalDistanceOffsetNorth
                )
            ) + controllerAxial.calculate(
                drive.follower.getVelocity().getXComponent(),
                drive.follower.getAcceleration().getXComponent()
            );

        if (isNaN(velocity) || nav.getGoalDistance() < DIST_MIN)
            velocity = FWD;

        return velocity;
    }
}
