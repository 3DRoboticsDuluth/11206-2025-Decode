package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class ConveyorSubsystem extends HardwareSubsystem {
    public static boolean TEL = false;
    public static double VEL = 1;

    public MotorEx motor;

    public ConveyorSubsystem() {
        motor = getMotor("conveyor", RPM_1150, this::configure);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;
        motor.setVelocityPercentage(VEL);
        motor.addTelemetry(TEL);
    }

    public void forward() {
        VEL = 0.5;
    }

    public void reverse() {
        VEL = -0.25;
    }

    public void stop() {
        VEL = 0;
    }

    private void configure(MotorEx motor) {
        motor.stopAndResetEncoder();
        motor.setZeroPowerBehavior(FLOAT);
        motor.setRunMode(VelocityControl);
    }
}
