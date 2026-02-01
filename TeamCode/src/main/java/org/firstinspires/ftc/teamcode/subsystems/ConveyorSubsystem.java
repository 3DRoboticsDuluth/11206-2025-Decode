package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Math.pow;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class ConveyorSubsystem extends HardwareSubsystem {
    public static double STOP = 0;
    public static double FWD = 0.5;
    public static double REV = -0.2;
    public static double VEL = STOP;
    public static boolean TEL = false;

    public MotorEx motor;

    public ConveyorSubsystem() {
        motor = getMotor("conveyor", RPM_1150, this::configure);
        VEL = STOP;
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;
        motor.setVelocityPercentage(VEL);
        motor.addTelemetry(TEL);
    }

    public void launch() {
        VEL = calculateVelocity();
    }

    public void forward() {
        VEL = FWD;
    }

    public void reverse() {
        VEL = REV;
    }

    public void stop() {
        VEL = STOP;
    }

    private double calculateVelocity() {
        return 1.158969 + (0.7886366 - 1.158969)/(1 + pow(nav.getGoalDistance() /116.6622, 2.41902));
    }

    private void configure(MotorEx motor) {
        motor.stopAndResetEncoder();
        motor.setZeroPowerBehavior(FLOAT);
        motor.setRunMode(VelocityControl);
    }
}
