package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
public class DeflectorSubsystem extends HardwareSubsystem {
    public static double MAX = 1;

    public static double MIN = 0;

    public static double POS = 0.5;

    public static double INC = 0.1;

    public Servo servo;

    public DeflectorSubsystem() {
        servo = getDevice(Servo.class, "deflector");
    }

    @Override
    public void periodic() {
        servo.setPosition(POS);
    }

    public void setPosition(double position) {
        POS = position;
    }

    public void up() {
        POS = clamp(POS + INC, MIN, MAX);
    }

    public void down() {
        POS = clamp(POS - INC, MIN, MAX);
    }
}
