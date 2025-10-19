package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.pedropathing.geometry.Pose;

public class PoseUtil {
    public static org.firstinspires.ftc.teamcode.adaptations.odometry.Pose fromPedroPose(Pose pose) {
        return new org.firstinspires.ftc.teamcode.adaptations.odometry.Pose(
            pose.getX(), pose.getY(), pose.getHeading()
        );
    }

    public static Pose toPedroPose(org.firstinspires.ftc.teamcode.adaptations.odometry.Pose pose) {
        return new Pose(
            pose.x, pose.y, pose.heading
        );
    }
}
