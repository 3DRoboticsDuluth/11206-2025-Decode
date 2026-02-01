package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Double.isNaN;
import static java.lang.Math.pow;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.ballistics.BallisticsModel;
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
        servo = getServo("deflector", s -> s.scaleRange(MIN, MAX, true));
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        POS = calculatePosition();

        if (isNaN(POS)) return;
        servo.set(POS);
        servo.addTelemetry(TEL);
    }

    public void up() {
        POS += INC;
    }

    public void down() {
        POS -= INC;
    }

    private double calculatePosition() {
        return 1.064487 + (0.2797805 - 1.064487) / (1 + pow(nav.getGoalDistance() / 177.5942, 3.400669));
    }
}
