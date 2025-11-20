package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;

@Configurable
public class GateSubsystem extends HardwareSubsystem {
    public static double CLOSE = 0.5;
    public static double OPEN = 1;
    public static double POS = OPEN;
    public static boolean TEL = false;

    public Servo servo;

    public GateSubsystem() {
        servo = getServo("gate");
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        servo.setPosition(
            POS = clamp(POS, CLOSE, OPEN)
        );

        servo.addTelemetry(TEL);
    }

    public void open() {
        POS = OPEN;
    }

    public void close() {
        POS = CLOSE;
    }
}
