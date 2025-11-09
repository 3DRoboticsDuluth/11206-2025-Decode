package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;

public class SortingSubsystem extends HardwareSubsystem{
    public static double SORT = 0.6;
    public static double PASS = 0.4;
    public static double POS = 0.5;
    public static boolean TEL = false;

    public Servo servo;

    public SortingSubsystem() {
        servo = getServo("sorter");
    }

    @Override
    public void periodic() {
        if (unready()) return;

        servo.setPosition(
            POS = clamp(POS, PASS, SORT)
        );

        servo.addTelemetry(TEL);
    }

    public void sort() {
        POS = SORT;
    }

    public void pass() {
        POS = PASS;
    }
}
