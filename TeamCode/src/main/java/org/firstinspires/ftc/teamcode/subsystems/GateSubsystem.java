package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
public class GateSubsystem extends HardwareSubsystem {
    public static double MAX = 1;
    public static double MIN = 0;
    public static double POS = 0.5;

    public Servo servo;

    public GateSubsystem() {
        servo = getDevice(Servo.class, "gate");
    }

    @Override
    public void periodic() {
        if (hasErrors()) return;
        servo.setPosition(
            POS = clamp(POS, MIN, MAX)
        );
    }

    public void open() {
        POS += MAX;
    }

    public void close() {
        POS -= MIN;
    }
}
