package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;

public class KickstandSubsystem extends HardwareSubsystem {
    public static double MIN = 0;
    public static double MAX = 1;
    public static double ENGAGE = 1;
    public static double DISENGAGE = 0;
    public static double POS = 0.5;
    public static boolean TEL = false;

    public ServoEx servoLeft;
    public ServoEx servoRight;

    public KickstandSubsystem() {
        servoLeft = getServo("kickstandLeft", MIN, MAX);
        servoRight = getServo("kickstandRight", MIN, MAX, s -> s.setInverted(true));
    }

    @Override
    public void periodic() {
        if (unready()) return;
        set(servoLeft);
        set(servoRight);
    }

    public void engage() {
        POS = ENGAGE;
    }

    public void disengage() {
        POS = DISENGAGE;
    }

    public void set(ServoEx servo) {
        servo.set(POS);
        servo.addTelemetry(TEL);
    }
}
