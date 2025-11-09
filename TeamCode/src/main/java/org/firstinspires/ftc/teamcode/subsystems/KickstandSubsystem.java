package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;

public class KickstandSubsystem extends HardwareSubsystem {
    public static double ENGAGE = 0.6;
    public static double DISENGAGE = 0.4;
    public static double POS = 0.5;
    public static boolean TEL = false;

    public Servo servo;

    public KickstandSubsystem() {
        servo = getServo("kickstand");
    }

    @Override
    public void periodic() {
        if (unready()) return;

        servo.setPosition(
            POS = clamp(POS, DISENGAGE, ENGAGE)
        );

        servo.addTelemetry(TEL);
    }

    public void engage() {
        POS = ENGAGE;
    }

    public void disengage() {
        POS = DISENGAGE;
    }
}
