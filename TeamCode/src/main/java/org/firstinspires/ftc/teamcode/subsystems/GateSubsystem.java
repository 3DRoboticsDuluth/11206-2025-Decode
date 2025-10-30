package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;

@Configurable
public class GateSubsystem extends HardwareSubsystem {
    public static boolean TEL = false;
    public static double MAX = 1;
    public static double MIN = 0;
    public static double POS = 0.5;

    public Servo servo;

    public GateSubsystem() {
        servo = getServo("gate");
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

    public void open() {
        POS = MAX;
    }

    public void close() {
        POS = MIN;
    }
}
