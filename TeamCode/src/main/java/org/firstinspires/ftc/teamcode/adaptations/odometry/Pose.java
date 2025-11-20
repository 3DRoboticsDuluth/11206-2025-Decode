package org.firstinspires.ftc.teamcode.adaptations.odometry;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

/** @noinspection unused*/
public class Pose {
    public double x;
    public double y;
    public double heading;

    public Pose(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = normalize(heading);
    }

    public Pose axial(double distance) {
        return new Pose(
            x + cos(heading) * distance,
            y + sin(heading) * distance,
            heading
        );
    }

    public Pose lateral(double distance) {
        return new Pose(
            x + cos(heading + PI / 2) * distance,
            y + sin(heading + PI / 2) * distance,
            heading
        );
    }

    public Pose turn(double degrees) {
        return new Pose(
            x, y, normalize(heading + toRadians(degrees))
        );
    }

    public Pose reverse() {
        return turn(180);
    }

    public Pose face(Pose pose) {
        return face(pose, 0);
    }

    public Pose face(Pose pose, double degrees) {
        return new Pose(x, y, normalize(atan2(pose) + toRadians(degrees)));
    }

    public double hypot() {
        return Math.hypot(this.x, this.y);
    }

    public double hypot(Pose pose) {
        return Math.hypot(
            pose.x - this.x,
            pose.y - this.y
        );
    }

    public double atan2(Pose pose) {
        return Math.atan2(pose.y - y, pose.x - x);
    }

    public double normalize(double heading) {
        if (heading > +PI) heading -= PI * 2;
        if (heading < -PI) heading += PI * 2;
        return heading;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    public String toString() {
        return String.format("%.1fx, %.1fy, %.1f°", x, y, toDegrees(heading));
    }
}
