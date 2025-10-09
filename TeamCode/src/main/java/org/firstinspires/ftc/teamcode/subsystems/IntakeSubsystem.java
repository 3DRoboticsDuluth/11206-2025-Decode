package org.firstinspires.ftc.teamcode.subsystems;

import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

public class IntakeSubsystem extends HardwareSubsystem {
    public static double VELOCITY;

    public MotorEx motor;

    public IntakeSubsystem() {
        motor = getMotor("intake", Motor.GoBILDA.RPM_1620);
    }

    @Override
    public void periodic() {
        motor.setVelocity(VELOCITY);
    }

    public void forward() {
        VELOCITY = 1;
    }

    public void reverse() {
        VELOCITY = -1;
    }

    public void stop() {
        VELOCITY = 0;
    }
}
