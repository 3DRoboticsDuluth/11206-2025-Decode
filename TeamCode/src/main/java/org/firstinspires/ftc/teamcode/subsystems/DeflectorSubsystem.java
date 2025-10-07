package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class DeflectorSubsystem extends HardwareSubsystem {
    public static double POSITION = 0.5;

    private final Servo deflector;

    public DeflectorSubsystem() {
        deflector = getDevice(Servo.class, "deflector");
    }

    public void setPosition() {
        // TODO
    }

    @Override
    public void periodic() {
        deflector.setPosition(POSITION);
    }
}
