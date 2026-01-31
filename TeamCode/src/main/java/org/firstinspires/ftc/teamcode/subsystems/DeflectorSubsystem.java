package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.robotcore.hardware.Servo.Direction.REVERSE;
import static java.lang.Double.isNaN;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;

@Configurable
public class DeflectorSubsystem extends HardwareSubsystem {
    public static double MIN = 0;
    public static double MAX = 0.8;
    public static double INC = 0.01;
    public static double POS = 0.5;
    public static boolean TEL = false;

    public ServoEx servo;

    public DeflectorSubsystem() {
        servo = getServo("deflector", s -> {
            //s.getServo().setDirection(REVERSE);
            s.getServo().scaleRange(MIN, MAX);
        });
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        /*POS = angleToPosition(
            BallisticsModel.deflectorAngle(
                config.pose.hypot(
                    nav.getGoalPose()
                )
            )
        );*/

        if (isNaN(POS)) return;
        servo.getServo().setPosition(POS);
        servo.addTelemetry(TEL);
    }

    public void up() {
        POS += INC;
    }

    public void down() {
        POS -= INC;
    }

    private double angleToPosition(double angle) {
        double numerator = -136987.8 - 0.5786442;
        double denominator = 1 + Math.pow(angle / 8.359162, 8.438698);
        return 0.5786442 + numerator / denominator;
    }
}
