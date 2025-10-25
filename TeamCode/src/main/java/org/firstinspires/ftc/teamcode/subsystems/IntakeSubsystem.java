package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1620;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class IntakeSubsystem extends HardwareSubsystem {
    public static boolean TEL = false;
    public static double VEL = 0;

    public MotorEx motor;

    public IntakeSubsystem() {
        motor = getMotor("intake", RPM_1620, this::configure);
    }

    @Override
    public void periodic() {
        if (unready()) return;
        motor.setVelocityPercentage(VEL);
        motor.addTelemetry("intake", TEL);
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
        motor.setInverted(true);
        motor.stopAndResetEncoder();
        motor.setRunMode(VelocityControl);
    }
}
