package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;

@Configurable
public class KickstandSubsystem extends HardwareSubsystem {
    public static double LEFT_MIN = 0.25;
    public static double LEFT_MAX = 0.95;
    public static double RIGHT_MIN = 0.00;
    public static double RIGHT_MAX = 0.70;
    public static double ENGAGE = 1;
    public static double DISENGAGE = 0;
    public static double POS = 0;
    public static boolean TEL = false;

    public ServoEx servoLeft;
    public ServoEx servoRight;

    public KickstandSubsystem() {
        servoLeft = getServo("kickstandLeft", LEFT_MIN, LEFT_MAX);
        servoRight = getServo("kickstandRight", RIGHT_MIN, RIGHT_MAX, s -> s.setInverted(true));
    }

    @Override
    public void periodic() {
//        if (unready()) return;
//        set(servoLeft);
//        set(servoRight);
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
