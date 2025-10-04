package org.firstinspires.ftc.teamcode.game;

import com.pedropathing.geometry.Pose;
import java.util.List;

public class NoGoZone {
    private final List<double[]> points;
    private final double penalty;
    private final ZoneType type;

    public NoGoZone(List<double[]> points, double penalty, ZoneType type) {
        this.points = points;
        this.penalty = penalty;
        this.type = type;
    }

    public double computePenalty(Pose p) {
        double x = p.getX();
        double y = p.getY();

        boolean inside = pointInPolygon(x, y, points);
        if (inside) {
            if (type == ZoneType.HARD) return Double.POSITIVE_INFINITY;
            return penalty;
        }

        if (type != ZoneType.HARD) {
            double minDist = distanceToPolygon(x, y, points);
            double maxRange = 10.0;
            if (minDist < maxRange) {
                return penalty * (1.0 - (minDist / maxRange));
            }
        }

        return 0.0;
    }

    private boolean pointInPolygon(double x, double y, List<double[]> poly) {
        boolean inside = false;
        int n = poly.size();
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = poly.get(i)[0], yi = poly.get(i)[1];
            double xj = poly.get(j)[0], yj = poly.get(j)[1];

            if (((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi + 1e-9) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }

    private double distanceToPolygon(double x, double y, List<double[]> poly) {
        double minDist = Double.POSITIVE_INFINITY;
        int n = poly.size();
        for (int i = 0; i < n; i++) {
            double[] a = poly.get(i);
            double[] b = poly.get((i + 1) % n);
            double dist = pointToSegmentDistance(x, y, a[0], a[1], b[0], b[1]);
            if (dist < minDist) minDist = dist;
        }
        return minDist;
    }

    private double pointToSegmentDistance(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            dx = px - x1;
            dy = py - y1;
            return Math.hypot(dx, dy);
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double projX = x1 + t * dx;
        double projY = y1 + t * dy;
        return Math.hypot(px - projX, py - projY);
    }
}
