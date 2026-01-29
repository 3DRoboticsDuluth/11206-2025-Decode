package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;

@Configurable
public class KickstandSubsystem extends HardwareSubsystem {
    public static double MIN = 0.25;
    public static double MAX = 0.95;
    public static double ENGAGE = 1;
    public static double DISENGAGE = 0;
    public static double POS = 0;
    public static boolean TEL = false;

    // Left:  Engage: 0.96, Disengage: 0.19 -> Difference: 0.77
    // Right: Engage: 0.30, Disengage: 1.00 -> Difference: 0.70

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

    private void set(ServoEx servo) {
        servo.set(POS);
        servo.addTelemetry(TEL);
    }
}
