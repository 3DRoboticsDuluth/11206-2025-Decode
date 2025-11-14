package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.BARE;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;

import static java.lang.Double.isNaN;

import android.annotation.SuppressLint;
import android.util.Log;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.adaptations.ballistics.BallisticsModel;
import org.firstinspires.ftc.teamcode.adaptations.ballistics.FlywheelController;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFCoefficients;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class FlywheelSubsystem extends HardwareSubsystem {
    public static FFCoefficients DEFAULT_FF = new FFCoefficients(0.00007, 0.000185, 0);
    public static PIDFCoefficients DEFAULT_PIDF = new PIDFCoefficients(0.001, 0, 0, 0);
    public static FFCoefficients LAUNCH_FF = new FFCoefficients(0.00007, 0.000185, 0);
    public static PIDFCoefficients LAUNCH_PIDF = new PIDFCoefficients(0.001, 0, 0, 0);
    public static FFCoefficients FF = DEFAULT_FF;
    public static PIDFCoefficients PIDF = DEFAULT_PIDF;
    public static FlywheelController controllerLeft = new FlywheelController(FF, PIDF);
    public static FlywheelController controllerRight = new FlywheelController(FF, PIDF);

    public static double RPM_MIN = 0;
    public static double RPM_MAX = 6000;
    public static double LAUNCH = 0.75 * RPM_MAX;
    public static double FWD = 0.5 * RPM_MAX;
    public static double REV = -0.5 * RPM_MAX;
    public static double HOLD = -0.2 * RPM_MAX;
    public static double STOP = 0;
    public static double THRESH = 0.8;
    public static double VEL = STOP;
    public static boolean TEL = false;

    public VoltageSensor voltageSensor;
    public MotorEx motorLeft;
    public MotorEx motorRight;

    public FlywheelSubsystem() {
        voltageSensor = hardwareMap.voltageSensor.iterator().next();
        motorLeft = getMotor("flywheelLeft", BARE, m -> configure(m, true));
        motorRight = getMotor("flywheelRight", BARE, m -> configure(m, false));
        FF = DEFAULT_FF;
        PIDF = DEFAULT_PIDF;
        VEL = STOP;
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        telemetry.addData("Flywheel (isLaunch)", () -> String.format("%b", isLaunch()));
        telemetry.addData("Flywheel (dist)", () -> String.format("%.1f", config.pose.hypot(nav.getGoalPose())));

        if (isLaunch())
            VEL = LAUNCH = BallisticsModel.flywheelRpm(
                config.pose.hypot(
                    nav.getGoalPose()
                )
            );

        double voltage = voltageSensor.getVoltage();
        set(motorLeft, controllerLeft, voltage);
        set(motorRight, controllerRight, voltage);

        //if (!TEL) return;

        telemetry.addData("Flywheel (RPM Min)", () -> String.format("%.1f", RPM_MIN));
        telemetry.addData("Flywheel (RPM Max)", () -> String.format("%.1f", RPM_MAX));
        telemetry.addData("Flywheel (VEL)", () -> String.format("%.1f", VEL));
    }

    public void launch() {
        setVelocity(LAUNCH, true);
    }

    public void forward() {
        setVelocity(FWD, false);
    }

    public void stop() {
        setVelocity(STOP, false);
    }

    public void reverse() {
        setVelocity(REV, false);
    }

    public void hold() {
        setVelocity(HOLD, false);
    }

    public boolean isReady() {
        return motorLeft.getRpm() >= VEL * THRESH &&
            motorRight.getRpm() >= VEL * THRESH;
    }

    private void setVelocity(double velocity, boolean launch) {
        Log.v(this.getClass().getSimpleName(), String.format("setVel | launch: %b", launch));
        FF = launch ? LAUNCH_FF : DEFAULT_FF;
        PIDF = launch ? LAUNCH_PIDF : DEFAULT_PIDF;
        VEL = velocity;
    }

    private boolean isLaunch() {
        return FF == LAUNCH_FF && PIDF == LAUNCH_PIDF;
    }

    private void configure(MotorEx motor, boolean inverted) {
        motor.setZeroPowerBehavior(FLOAT);
        motor.setInverted(inverted);
    }

    private void set(MotorEx motor, FlywheelController controller, double voltage) {
        if (isNaN(VEL)) return;
        controller.setCoefficients(FF, PIDF);
        controller.setTarget(VEL);
        motor.set(controller.update(motor.getRpm(), voltage));
        motor.addTelemetry(TEL);
    }
}
