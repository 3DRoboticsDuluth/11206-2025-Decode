package org.firstinspires.ftc.teamcode.game;

public class Pose {
    public double x;
    public double y;
    public double heading;

    public Pose(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
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
}
