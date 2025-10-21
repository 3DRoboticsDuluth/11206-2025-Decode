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
        if (hasErrors()) return;
        servo.setPosition(
            POS = clamp(POS, MIN, MAX)
        );
    }

    public void up() {
        POS += INC;
    }

    public void down() {
        POS -= INC;
    }
}
