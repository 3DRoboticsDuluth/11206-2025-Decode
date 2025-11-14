package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;

import static java.lang.Double.isNaN;

import android.annotation.SuppressLint;
import android.util.Log;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.balistics.ShooterModel;
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

        double angle = 0;

        POS = angleToPosition(
            ShooterModel.bestAngleDeg(
                angle = config.pose.hypot(
                    nav.getStartPose() // TODO: replace with getGoalPose
                )
            )
        );

        Log.v(this.getClass().getSimpleName(), String.format("angle: %.2f, POS: %.2f", angle, POS));

        if (isNaN(POS)) return;

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

    public static double angleToPosition(double x) {
        return -0.3835821
                + 0.03225259 * x
                + 0.03225259 * x * x;
    }
}
