package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.BARE;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@Configurable
public class FlywheelSubsystem extends HardwareSubsystem {
    public static double VEL = 0;
    public static double KS = 0;
    public static double KA = 0;
    public static double KV = 0;

    public MotorEx motorLeft;
    public MotorEx motorRight;

    public FlywheelSubsystem() {
        motorLeft = getMotor("flywheelLeft", BARE, this::configure);
        motorRight = getMotor("flywheelRight", BARE, this::configure);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (hasErrors()) return;
        motorLeft.setFeedforwardCoefficients(KS, KA, KV);
        motorRight.setFeedforwardCoefficients(KS, KA, KV);
        motorLeft.setVelocity(VEL);
        motorRight.setVelocity(VEL);
    }

    public void start() {
        VEL = 0.5;
    }

    public void stop() {
        VEL = 0;
    }

    private void configure(MotorEx motor) {
        motor.setRunMode(VelocityControl);

        double[] coefficients = motor.getFeedforwardCoefficients();

        KS = coefficients[0];
        KA = coefficients[1];
        KV = coefficients[2];
    }
}
