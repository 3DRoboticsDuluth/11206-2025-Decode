package org.firstinspires.ftc.teamcode.subsystems;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;

@Configurable
public class GateSubsystem extends HardwareSubsystem {
    public static double MIN = 0;
    public static double MAX = 0.5;
    public static double CLOSE = 0;
    public static double HOLD = 0.6;
    public static double OPEN = 1;
    public static double POS = CLOSE;
    public static boolean TEL = false;

    public ServoEx servo;

    public GateSubsystem() {
        servo = getServo("gate", s -> s.scaleRange(MIN, MAX, true));
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;
        servo.set(POS);
        servo.addTelemetry(TEL);
    }

    public void open() {
        POS = OPEN;
    }

    public void hold() {
        POS = HOLD;
    }

    public void close() {
        POS = CLOSE;
    }
}
