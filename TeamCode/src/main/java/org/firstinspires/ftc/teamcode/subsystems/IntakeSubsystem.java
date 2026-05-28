package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.robotcore.hardware.DigitalChannel.Mode.INPUT;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.adaptations.util.Debounce;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class IntakeSubsystem extends HardwareSubsystem {
    public static final double STOP = 0;
    public static double HOLD = 0.50;
    public static double FWD = 0.75;
    public static double REV = -0.25;
    public static double VEL = STOP;
    public static int MAX_ARTIFACTS = 3;
    public static double LASER_THRESH = 0.125;
    public static boolean TEL = false;

    public MotorEx motor;
    public DigitalChannel laser;
    public boolean full = false;
    public int artifacts = 0;
    public Debounce laserDebounce = new Debounce();

    public IntakeSubsystem() {
        motor = getMotor("intake", RPM_1150, this::configure);
        laser = getDevice(DigitalChannel.class, "laser2", l -> l.setMode(INPUT));
        VEL = STOP;
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        motor.setVelocityPercentage(VEL);

        boolean laserCurrent = laser.getState();
        if (laserDebounce.triggered(laserCurrent, LASER_THRESH) && artifacts < MAX_ARTIFACTS)
            full = ++artifacts >= MAX_ARTIFACTS;

        motor.addTelemetry(TEL);

        telemetry.addData("Intake (Artifacts)", () -> String.format("%d", artifacts));
        telemetry.addData("Intake (Laser)", () -> laserCurrent ? "1" : "0");
    }

    public void forward() {
        VEL = FWD;
    }

    public void reverse() {
        VEL = REV;
    }

    public void hold() {
        VEL = HOLD;
    }

    public void stop() {
        VEL = STOP;
    }

    public void reset() {
        artifacts = 0;
        full = false;
    }

    private void configure(MotorEx motor) {
        motor.setInverted(true);
        motor.stopAndResetEncoder();
        motor.setZeroPowerBehavior(FLOAT);
        motor.setRunMode(VelocityControl);
    }
}
