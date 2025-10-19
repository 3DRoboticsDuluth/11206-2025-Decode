package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1620;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;

import com.bylazar.configurables.annotations.Configurable;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@Configurable
public class IntakeSubsystem extends HardwareSubsystem {
    public static double VEL = 0;

    public MotorEx motor;

    public IntakeSubsystem() {
        motor = getMotor("intake", RPM_1620, m -> m.setRunMode(VelocityControl));
    }

    @Override
    public void periodic() {
        if (hasErrors()) return;
        motor.setVelocity(VEL);
    }

    public void forward() {
        VEL = 1;
    }

    public void reverse() {
        VEL = -1;
    }

    public void stop() {
        VEL = 0;
    }
}
