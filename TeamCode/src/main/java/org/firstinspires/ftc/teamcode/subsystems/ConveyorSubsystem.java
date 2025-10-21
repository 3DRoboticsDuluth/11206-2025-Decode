package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class ConveyorSubsystem extends HardwareSubsystem {
    public static boolean TELEM = false;
    public static double VEL = 0;

    public MotorEx motor;

    public ConveyorSubsystem() {
        motor = getMotor("conveyorLeft", RPM_1150, this::configure);
        motor = getMotor("conveyorRight", RPM_1150, this::configure);
    }

    @Override
    public void periodic() {
        if (hasErrors()) return;
        motor.setVelocityPercentage(VEL);
        motor.addTelemetry("conveyorLeft", TELEM);
        motor.addTelemetry("conveyorRight", TELEM);
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