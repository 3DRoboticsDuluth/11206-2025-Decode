package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Double.isNaN;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.adaptations.ballistics.BallisticsModel;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;

@Configurable
public class DeflectorSubsystem extends HardwareSubsystem {
    public static double MIN = 0.50;
    public static double MAX = 0.57;
    public static double MID = 0.535;
    public static double INC = 0.0025;
    public static double POS = MID;
    public static boolean TEL = false;

    public ServoEx servo;

    public DeflectorSubsystem() {
        servo = getServo("deflector", MIN, MAX);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        POS = angleToPosition(
            BallisticsModel.deflectorAngle(
                config.pose.hypot(
                    nav.getGoalPose()
                )
            )
        );

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

    public void compensate() {
        POS = MID;
    }

    private double angleToPosition(double angle) {
        double numerator = -136987.8 - 0.5786442;
        double denominator = 1 + Math.pow(angle / 8.359162, 8.438698);
        return 0.5786442 + numerator / denominator;
    }
}
