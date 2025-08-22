package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.pedropathing.localization.Pose;

public class PoseUtil {
    public static org.firstinspires.ftc.teamcode.game.Pose fromPedroPose(Pose pose) {
        return new org.firstinspires.ftc.teamcode.game.Pose(
            pose.getX(), pose.getY(), pose.getHeading()
        );
    }

    public static Pose toPedroPose(org.firstinspires.ftc.teamcode.game.Pose pose) {
        return new Pose(
            pose.x, pose.y, pose.heading
        );
    }
}
