package org.firstinspires.ftc.teamcode.adaptations.balistics;

import static java.lang.Math.*;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;

/**
 * Enhanced shooter model that accounts for robot velocity during launch.
 * This allows accurate scoring while the robot is moving.
 */
@Configurable
public class MovingShooterModel {

    // ======= INHERIT BASE CONSTANTS FROM ShooterModel =======
    
    // Minimum velocity threshold to consider robot as "moving" (in/s)
    public static double MOVING_THRESHOLD = 2.0;
    
    // Time delay between calculation and actual launch (seconds)
    public static double LAUNCH_DELAY = 0.15;
    
    // Maximum iterations for iterative solution
    public static int MAX_ITERATIONS = 20;
    
    // Convergence tolerance (inches)
    public static double CONVERGENCE_TOL = 0.1;

    /**
     * Calculate shooting parameters accounting for robot motion.
     *
     * @param robotPose Current robot position
     * @param targetPose Target position
     * @param robotVelX Robot velocity in X direction (in/s)
     * @param robotVelY Robot velocity in Y direction (in/s)
     * @return ShotParameters with angle and RPM, or null if infeasible
     */
    public static ShotParameters calculateMovingShot(
        Pose robotPose, 
        Pose targetPose,
        double robotVelX,
        double robotVelY
    ) {
        // Check if robot is moving significantly
        double robotSpeed = hypot(robotVelX, robotVelY);
        if (robotSpeed < MOVING_THRESHOLD) {
            return calculateStationaryShot(robotPose, targetPose);
        }

        // Predict robot position at launch time
        double launchX = robotPose.x + robotVelX * LAUNCH_DELAY;
        double launchY = robotPose.y + robotVelY * LAUNCH_DELAY;
        
        // Initial distance calculation
        double dx = targetPose.x - launchX;
        double dy = targetPose.y - launchY;
        double distanceToTarget = hypot(dx, dy);
        
        // Direction to target
        double angleToTarget = atan2(dy, dx);
        
        // Project robot velocity onto shooting direction
        double velTowardTarget = robotVelX * cos(angleToTarget) + 
                                 robotVelY * sin(angleToTarget);
        double velPerpendicularTarget = -robotVelX * sin(angleToTarget) + 
                                        robotVelY * cos(angleToTarget);
        
        // Iteratively solve for flight time accounting for robot motion
        ShotParameters params = iterativeSolution(
            distanceToTarget,
            velTowardTarget,
            velPerpendicularTarget,
            robotPose.heading,
            angleToTarget
        );
        
        if (params == null) {
            // Fallback to stationary calculation
            return calculateStationaryShot(robotPose, targetPose);
        }
        
        return params;
    }

    private static ShotParameters iterativeSolution(
        double distance,
        double velToward,
        double velPerp,
        double robotHeading,
        double angleToTarget
    ) {
        double currentDist = distance;
        ShotParameters bestParams = null;
        
        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            // Find best angle for current estimated distance
            double bestAngle = ShooterModel.bestAngleDeg(currentDist);
            if (Double.isNaN(bestAngle)) return null;
            
            // Attain RPM
            double rpm = ShooterModel.recommendedRPM(currentDist);
            if (Double.isNaN(rpm)) return null;
            
            // Projectile Velo
            double launchAngleRad = toRadians(bestAngle);
            double v = calculateLaunchVelocity(rpm);
            double vx = v * cos(launchAngleRad);
            double vy = v * sin(launchAngleRad);
            
            // Time of flight
            double deltaY = ShooterModel.TARGET_HEIGHT_IN - ShooterModel.SHOOTER_HEIGHT_IN;
            double flightTime = calculateFlightTime(vx, vy, deltaY, currentDist);
            
            if (Double.isNaN(flightTime) || flightTime <= 0) return null;
            
            // Calculate how far robot moves during flight
            double robotDriftParallel = velToward * flightTime;
            double robotDriftPerp = velPerp * flightTime;
            
            // Effective distance accounting for robot motion
            // We need to shoot at where target WILL BE relative to where WE WILL BE
            double effectiveDist = hypot(
                distance - robotDriftParallel,
                robotDriftPerp
            );
            
            // Check convergence
            if (abs(effectiveDist - currentDist) < CONVERGENCE_TOL) {
                // Calculate heading adjustment for perpendicular drift
                double headingAdjust = atan2(robotDriftPerp, distance - robotDriftParallel);
                double absoluteAngle = angleToTarget + headingAdjust;
                double relativeAngle = normalizeAngle(absoluteAngle - robotHeading);
                
                bestParams = new ShotParameters(
                    bestAngle,
                    rpm,
                    toDegrees(relativeAngle),
                    flightTime
                );
                break;
            }
            
            currentDist = effectiveDist;
        }
        
        return bestParams;
    }

    /**
     * Calculate launch velocity from RPM.
     */
    private static double calculateLaunchVelocity(double rpm) {
        // v = RPM * (2π * r) / 60, accounting for efficiency
        double wheelCircumference = 2 * PI * ShooterModel.WHEEL_RADIUS_IN;
        return (rpm * wheelCircumference / 60.0) * ShooterModel.K_EFF;
    }

    /**
     * Estimate flight time using quadratic formula for parabolic trajectory.
     */
    private static double calculateFlightTime(double vx, double vy, double deltaY, double distance) {
        // Horizontal time: t = distance / vx
        double tHoriz = distance / vx;
        
        // Vertical equation: deltaY = vy*t - 0.5*g*t^2
        // Solve: 0.5*g*t^2 - vy*t + deltaY = 0
        double a = 0.5 * ShooterModel.G_IN_PER_S2;
        double b = -vy;
        double c = deltaY;
        double discriminant = b*b - 4*a*c;
        
        if (discriminant < 0) return Double.NaN;
        
        double t1 = (-b + sqrt(discriminant)) / (2*a);
        double t2 = (-b - sqrt(discriminant)) / (2*a);
        
        // Use the positive time closest to horizontal time
        double tVert = (t1 > 0 && t2 > 0) ? min(t1, t2) : max(t1, t2);
        
        // Average the two estimates (they should be close)
        return (tHoriz + tVert) / 2.0;
    }

    /**
     * Fallback to stationary shot calculation.
     */
    private static ShotParameters calculateStationaryShot(Pose robotPose, Pose targetPose) {
        double dx = targetPose.x - robotPose.x;
        double dy = targetPose.y - robotPose.y;
        double distance = hypot(dx, dy);
        
        double angle = ShooterModel.bestAngleDeg(distance);
        if (Double.isNaN(angle)) return null;
        
        double rpm = ShooterModel.recommendedRPM(distance);
        if (Double.isNaN(rpm)) return null;
        
        double angleToTarget = atan2(dy, dx);
        double relativeAngle = normalizeAngle(angleToTarget - robotPose.heading);
        
        return new ShotParameters(angle, rpm, toDegrees(relativeAngle), 0);
    }

    /**
     * Normalize angle to [-π, π].
     */
    private static double normalizeAngle(double angle) {
        while (angle > PI) angle -= 2*PI;
        while (angle < -PI) angle += 2*PI;
        return angle;
    }

    /**
     * Container for shot parameters.
     */
    public static class ShotParameters {
        public final double deflectorAngleDeg;  // Launch angle
        public final double flywheelRPM;        // Flywheel speed
        public final double headingAdjustDeg;   // How much to turn robot
        public final double flightTime;         // Estimated flight time
        
        public ShotParameters(double angle, double rpm, double heading, double time) {
            this.deflectorAngleDeg = angle;
            this.flywheelRPM = rpm;
            this.headingAdjustDeg = heading;
            this.flightTime = time;
        }
        
        public boolean isValid() {
            return !Double.isNaN(deflectorAngleDeg) && 
                   !Double.isNaN(flywheelRPM) &&
                   flywheelRPM > 0;
        }
        
        public boolean needsHeadingAdjust(double tolerance) {
            return abs(headingAdjustDeg) > tolerance;
        }
    }
}