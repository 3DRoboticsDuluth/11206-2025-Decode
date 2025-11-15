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
    public static double MID = 0.53;  // ✅ Midpoint of range
    public static double INC = 0.0025;
    public static double POS = MID;
    public static boolean TEL = false;

    // ✅ Control mode - allow external commands to override auto-calculation
    public static boolean AUTO_MODE = true;

    public Servo servo;

    public DeflectorSubsystem() {
        servo = getServo("deflector");
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        // ✅ Only auto-calculate if not being controlled by moving shot system
        if (AUTO_MODE) {
            // Calculate distance to goal
            double distanceToGoal = config.pose.hypot(nav.getGoalPose());  // ✅ Fixed to use goal

            // Get optimal angle for this distance
            double optimalAngle = ShooterModel.bestAngleDeg(distanceToGoal);

            // Convert to servo position
            if (!isNaN(optimalAngle)) {
                POS = angleToPosition(optimalAngle);
            }

            Log.v(this.getClass().getSimpleName(),
                    String.format("dist: %.2f, angle: %.2f°, pos: %.3f",
                            distanceToGoal, optimalAngle, POS));
        }

        // Set servo position (clamped to safe range)
        servo.setPosition(clamp(POS, MIN, MAX));

        servo.addTelemetry(TEL);
    }

    public void up() {
        AUTO_MODE = false;  // ✅ Manual control disables auto
        POS += INC;
    }

    public void down() {
        AUTO_MODE = false;
        POS -= INC;
    }

    public void compensate() {
        AUTO_MODE = false;
        POS = MID;
    }

    public void enableAuto() {
        AUTO_MODE = true;
    }
    public static double angleToPosition(double angleDeg) {
        double angleMin = 42.0;  // Minimum deflector angle
        double angleMax = 60.0;  // Maximum deflector angle

        // Normalize to 0-1
        double normalized = (angleDeg - angleMin) / (angleMax - angleMin);

        // Map to servo range
        return MIN + normalized * (MAX - MIN);
    }
}