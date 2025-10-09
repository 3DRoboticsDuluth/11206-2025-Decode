package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class DeflectorSubsystem extends HardwareSubsystem {
    public static double POSITION = 0.5;

    public Servo servo;

    public DeflectorSubsystem() {
        servo = getDevice(Servo.class, "deflector");
    }

    public void setPosition(double position) {
        POSITION = position;
    }

    @Override
    public void periodic() {
        servo.setPosition(POSITION);
    }
}
