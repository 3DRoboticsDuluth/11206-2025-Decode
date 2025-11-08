package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class ConveyorSubsystem extends HardwareSubsystem {
    public static final double STOP = 0;
    public static double FWD = 0.5;
    public static double REV = -0.25;
    public static double VEL = STOP;
    public static double LAUNCH = 1;
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
        VEL = LAUNCH;
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

    private void configure(MotorEx motor) {
        motor.stopAndResetEncoder();
        motor.setZeroPowerBehavior(FLOAT);
        motor.setRunMode(VelocityControl);
    }
}
