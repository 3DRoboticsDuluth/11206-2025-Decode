package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import com.qualcomm.robotcore.hardware.Servo;

public class KickstandSubsystem extends HardwareSubsystem{
    public static double ENGAGE = 1;
    public static double DISENGAGE = 0;
    public static double POS = 0.5;


    public Servo servo;

    public KickstandSubsystem() {
        servo = getDevice(Servo.class, "kickstand");
    }

    @Override
    public void periodic() {
        if (unready()) return;
        servo.setPosition(
            POS = clamp(POS, DISENGAGE, ENGAGE)
        );
    }

    public void engage() {
        POS = ENGAGE;
    }

    public void disengage() {
        POS = DISENGAGE;
    }
}
