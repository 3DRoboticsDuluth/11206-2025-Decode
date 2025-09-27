package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;

public class CurveOptimization {

    // Creates rough BezierCurve to be refined later
    public static BezierCurve generate(Pose start, Pose target) {

        // range to cover full field diagonal (~200+ inches)
        double d1min = 5.0, d1max = 120.0;
        double d2min = 5.0, d2max = 120.0;

        BezierCurve best = null;
        double bestScore = Double.POSITIVE_INFINITY;

        int grid = 20;

        for (int i = 0; i <= grid; i++) {
            double d1 = d1min + (d1max - d1min) * i / grid;
            for (int j = 0; j <= grid; j++) {
                double d2 = d2min + (d2max - d2min) * j / grid;

                Pose c1 = offsetAlongHeading(start, d1, true);
                Pose c2 = offsetAlongHeading(target, d2, false);

                BezierCurve candidate = new BezierCurve(new Pose[]{ start, c1, c2, target });
                double sc = score(candidate);
                if (sc < bestScore) {
                    bestScore = sc;
                    best = candidate;
                }
            }
        }

// Refines the BezierCurve previously generated
        double step = 8.0;
        for (int it = 0; it < 20; it++) {
            boolean improved = false;
            double[] deltas = new double[]{ -step, 0, +step };
            for (double dx : deltas) {
                for (double dy : deltas) {
                    double base1 = controlDistance(best, true);
                    double base2 = controlDistance(best, false);
                    double nd1 = clamp(d1min, d1max, base1 + dx);
                    double nd2 = clamp(d2min, d2max, base2 + dy);

                    Pose c1 = offsetAlongHeading(start, nd1, true);
                    Pose c2 = offsetAlongHeading(target, nd2, false);

                    BezierCurve cand = new BezierCurve(new Pose[]{ start, c1, c2, target });
                    double sc = score(cand);
                    if (sc < bestScore) {
                        bestScore = sc;
                        best = cand;
                        improved = true;
                    }
                }
            }
            if (!improved) {
                step *= 0.5;
            }
        }

        return best;
    }

    public static BezierCurve generate(org.firstinspires.ftc.teamcode.game.Pose start,
                                       org.firstinspires.ftc.teamcode.game.Pose target) {
        return generate(toPedroPose(start), toPedroPose(target));
    }

    private static Pose toPedroPose(org.firstinspires.ftc.teamcode.game.Pose p) {
        return new Pose(p.getX(), p.getY(), p.getHeading());
    }

    private static double clamp(double lo, double hi, double v) {
        if (v < lo) return lo;
        if (v > hi) return hi;
        return v;
    }

    private static double controlDistance(BezierCurve curve, boolean firstControl) {
        Pose[] cps = curve.getControlPoints().toArray(new Pose[0]);
        Pose start = cps[0];
        Pose ctrl = firstControl ? cps[1] : cps[2];
        return Math.hypot(ctrl.getX() - start.getX(), ctrl.getY() - start.getY());
    }

    private static Pose offsetAlongHeading(Pose p, double dist, boolean forward) {
        double hx = Math.cos(p.getHeading());
        double hy = Math.sin(p.getHeading());
        double sign = forward ? 1.0 : -1.0;
        return new Pose(
            p.getX() + sign * dist * hx,
            p.getY() + sign * dist * hy,
            p.getHeading()
        );
    }

    public static double score(BezierCurve curve) {
        double totalScore = 0.0;
        int samples = 500;

        Pose last = sample(curve, 0.0);

        // drivetrain constants for weighting
        double forwardAccel = Math.abs(Constants.followerConstants.forwardZeroPowerAcceleration);
        double lateralAccel = Math.abs(Constants.followerConstants.lateralZeroPowerAcceleration);

        double forwardWeight = 1.0 / Math.max(forwardAccel, 1e-6);
        double lateralWeight = 1.0 / Math.max(lateralAccel, 1e-6);

        forwardWeight *= 50.0;
        lateralWeight *= 50.0;

        for (int i = 1; i <= samples; i++) {
            double t = i / (double) samples;
            Pose now = sample(curve, t);

            double dx = now.getX() - last.getX();
            double dy = now.getY() - last.getY();

            double heading = last.getHeading();
            double forward =  dx * Math.cos(heading) + dy * Math.sin(heading);
            double lateral = -dx * Math.sin(heading) + dy * Math.cos(heading);

            totalScore += Math.abs(forward) * forwardWeight
                    + Math.abs(lateral) * lateralWeight;

            // penalize sharp turns
            double dHeading = angleDiff(now.getHeading(), last.getHeading());
            double stepLength = Math.hypot(dx, dy);
            totalScore += (Math.abs(dHeading) / (stepLength + 1e-6)) * 5.0;

            last = now;
        }

        return totalScore;
    }

    public static double angleDiff(double a, double b) { // Just incase its like 178 to -178 so it calcs right since it would be bad for the score if it didn't
        double d = a - b;
        while (d > Math.PI) d -= 2 * Math.PI;
        while (d < -Math.PI) d += 2 * Math.PI;
        return d;
    }

    private static double computeMaxCurvature(BezierCurve curve) {
        int samples = 30;
        double maxC = 0.0;
        for (int i = 1; i < samples; i++) {
            double t0 = (i - 1) / (double) samples;
            double t1 = i / (double) samples;
            double t2 = (i + 1) / (double) samples;

            Pose p0 = sample(curve, t0);
            Pose p1 = sample(curve, t1);
            Pose p2 = sample(curve, t2);

            double dx1 = p1.getX() - p0.getX();
            double dy1 = p1.getY() - p0.getY();
            double dx2 = p2.getX() - p1.getX();
            double dy2 = p2.getY() - p1.getY();

            double ddx = dx2 - dx1;
            double ddy = dy2 - dy1;
            double num = Math.abs(dx1 * ddy - dy1 * ddx);
            double denom = Math.pow(dx1*dx1 + dy1*dy1, 1.5) + 1e-9;
            double curv = num / denom;
            if (curv > maxC) maxC = curv;
        }
        return maxC;
    }

    public static Pose sample(BezierCurve curve, double t) {
        Pose[] cp = curve.getControlPoints().toArray(new Pose[0]);
        double x = bezierInterp(cp[0].getX(), cp[1].getX(), cp[2].getX(), cp[3].getX(), t);
        double y = bezierInterp(cp[0].getY(), cp[1].getY(), cp[2].getY(), cp[3].getY(), t);

        double dx = bezierDerivative(cp[0].getX(), cp[1].getX(), cp[2].getX(), cp[3].getX(), t);
        double dy = bezierDerivative(cp[0].getY(), cp[1].getY(), cp[2].getY(), cp[3].getY(), t);

        double heading = Math.atan2(dy, dx);
        return new Pose(x, y, heading);
    }

    private static double bezierInterp /* Interpolation */ (double a, double b, double c, double d, double t) {
        double u = 1 - t;
        return (u*u*u)*a + 3*(u*u)*t*b + 3*u*(t*t)*c + (t*t*t)*d;
    }

    private static double bezierDerivative(double a, double b, double c, double d, double t) {
        double u = 1 - t;
        return 3*(u*u)*(b - a) + 6*u*t*(c - b) + 3*(t*t)*(d - c);
    }
}
