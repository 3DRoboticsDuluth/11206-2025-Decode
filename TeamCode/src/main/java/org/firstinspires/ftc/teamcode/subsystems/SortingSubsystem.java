package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import com.qualcomm.robotcore.hardware.Servo;

public class SortingSubsystem extends HardwareSubsystem{
    public static double SORT = 1;
    public static double PASS = 0;
    public static double POS = .5;

    public Servo servo;

    public SortingSubsystem() {
        servo = getDevice(Servo.class, "sorter");
    }

    @Override
    public void periodic() {
        if (unready()) return;
        servo.setPosition(
            POS = clamp(POS, PASS, SORT)
        );
    }

    public void sort() {
        servo.setPosition(POS = SORT);
    }

    public void pass() {
        servo.setPosition(POS = PASS);
    }
}
