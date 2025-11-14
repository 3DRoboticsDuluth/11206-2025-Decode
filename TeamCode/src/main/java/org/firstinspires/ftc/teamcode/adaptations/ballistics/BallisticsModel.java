package org.firstinspires.ftc.teamcode.adaptations.ballistics;

import static java.lang.Math.*;

import com.bylazar.configurables.annotations.Configurable;

@Configurable
public class BallisticsModel {
    // Geometry (inches)
    public static double SHOOTER_HEIGHT_IN = 11.5; // launcher (ball center) height
    public static double TARGET_HEIGHT_IN = 43.0; // aim height on goal plane (use 38.75 if aiming bottom)
    public static double WHEEL_RADIUS_IN = 96.0 / 25.4 / 2.0; // 96 mm dia => ~1.88976 in

    // Angle limits (degrees) — set to your deflector range
    public static double ANGLE_MIN_DEG = 46.0;
    public static double ANGLE_MAX_DEG = 58.4;
    public static double ANGLE_STEP_DEG = 0.1;

    // Physics (inches)
    public static double G_IN_PER_S2 = 386.09; // gravity

    // Efficiency / calibration
    public static double K_EFF = 0.425; // wheel→ball speed ratio (0<k≤1). Raise RPM if real shots fall short.
    public static double THETA_OFFSET_D = 0.0; // constant angle offset (deg) to map deflector reading → true launch angle
    public static double KV_SCALE = 1.00; // global RPM scale (e.g., 1.10 for +10%)
    public static double BETA_RPM2 = 0.00000002; // optional tiny quadratic bump; start at 0.0 (e.g., 5e-9 if needed)

    public static double DISTANCE_OVERRIDE = 0;

    public static double deflectorAngle(double distance) {
        if (DISTANCE_OVERRIDE != 0) distance = DISTANCE_OVERRIDE;

        double bestAng = Double.NaN;
        double bestRpm = Double.POSITIVE_INFINITY;

        for (double a = ANGLE_MIN_DEG; a <= ANGLE_MAX_DEG + 1e-9; a += ANGLE_STEP_DEG) {
            double rpm = rpmCalibrated(distance, a);
            if (!Double.isNaN(rpm)) {
                if (rpm < bestRpm - 1e-6 || (abs(rpm - bestRpm) <= 1e-6 && a > bestAng)) {
                    bestRpm = rpm;
                    bestAng = a; // tie-break toward higher angle (gentler entry)
                }
            }
        }

        return bestAng;
    }

    public static double flywheelRpm(double distance) {
        if (DISTANCE_OVERRIDE != 0) distance = DISTANCE_OVERRIDE;
        double ang = deflectorAngle(distance);
        if (Double.isNaN(ang)) return Double.NaN;
        return rpmCalibrated(distance, ang);
    }

    // Calibrated RPM = (ideal RPM) * KV_SCALE * (1 + BETA * idealRPM^2)
    private static double rpmCalibrated(double distance, double angle) {
        double rpmIdeal = rpmIdeal(distance, angle);
        if (Double.isNaN(rpmIdeal)) return Double.NaN;
        double rpm = rpmIdeal * KV_SCALE;
        if (BETA_RPM2 > 0.0) rpm *= (1.0 + BETA_RPM2 * rpmIdeal * rpmIdeal);
        return rpm;
    }

    // Closed-form ideal RPM (no extra scaling beyond K_EFF & THETA_OFFSET_D). NaN if infeasible.
    private static double rpmIdeal(double distance, double angle) {
        final double deltaY = TARGET_HEIGHT_IN - SHOOTER_HEIGHT_IN;
        final double alpha  = toRadians(angle + THETA_OFFSET_D);
        final double tanA = tan(alpha);
        final double cosA = cos(alpha);
        final double denom = 2.0 * cosA * cosA * (distance * tanA - deltaY);
        if (denom <= 0.0) return Double.NaN; // infeasible at any speed
        final double v2 = (G_IN_PER_S2 * distance * distance) / denom; // (in/s)^2
        if (v2 <= 0.0) return Double.NaN;
        final double v = sqrt(v2); // in/s
        // RPM = (30 * v) / (π * k * r)
        return (30.0 * v) / (PI * K_EFF * WHEEL_RADIUS_IN);
    }
}
