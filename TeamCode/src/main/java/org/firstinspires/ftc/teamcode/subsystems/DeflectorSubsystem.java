package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;

@Configurable
public class DeflectorSubsystem extends HardwareSubsystem {
    public static double MIN = 0.49;
    public static double MAX = 0.57;
    public static double MID = 0.5;
    public static double INC = 0.0025;
    public static double POS = 0.5;
    public static boolean TEL = false;

    public Servo servo;

    public DeflectorSubsystem() {
        servo = getServo("deflector");
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        servo.setPosition(
            POS = clamp(POS, MIN, MAX)
        );

        servo.addTelemetry(TEL);
    }

    public void up() {
        POS += INC;
    }

    public void down() {
        POS -= INC;
    }

    public void compensate() {
        POS = MID;
    }
}
