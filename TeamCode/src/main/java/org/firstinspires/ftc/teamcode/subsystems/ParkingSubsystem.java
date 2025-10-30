package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

public class ParkingSubsystem extends HardwareSubsystem{
    public static double UP = 1;
    public static double DOWN = 0;

    public Servo servo;

    public ParkingSubsystem() {
        servo = getDevice(Servo.class, "park");
    }

    @Override
    public void periodic() {
    }

    public void park() {
        servo.setPosition(UP);
    }

    public void unPark() {
        servo.setPosition(DOWN);
    }
}
