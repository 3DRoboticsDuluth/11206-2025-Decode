package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1620;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class IntakeSubsystem extends HardwareSubsystem {
    public static final double STOP = 0;
    public static double HOLD = 0.1;
    public static double FWD = 0.5;
    public static double REV = -0.25;
    public static double VEL = STOP;
    public static boolean TEL = false;

    public MotorEx motor;

    public IntakeSubsystem() {
        motor = getMotor("intake", RPM_1620, this::configure);
    }

    @Override
    public void periodic() {
        if (unready()) return;
        motor.setVelocityPercentage(VEL);
        motor.addTelemetry(TEL);
    }

    public void forward() {
        VEL = FWD;
    }

    public void reverse() {
        VEL = REV;
    }

    public void hold(){VEL = HOLD;}

    public void stop() {
        VEL = STOP;
    }

    private void configure(MotorEx motor) {
        motor.setInverted(true);
        motor.stopAndResetEncoder();
        motor.setZeroPowerBehavior(FLOAT);
        motor.setRunMode(VelocityControl);
    }
}
