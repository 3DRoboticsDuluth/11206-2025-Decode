package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

public class SortingSubsystem extends HardwareSubsystem{
    public static double SORT = 1;
    public static double PASS = 0;

    public Servo servo;

    public SortingSubsystem() {
        servo = getDevice(Servo.class, "sorter");
    }

    @Override
    public void periodic() {

    }

    public void sort() {
        servo.setPosition(SORT);
    }

    public void pass() {
        servo.setPosition(PASS);
    }
}
