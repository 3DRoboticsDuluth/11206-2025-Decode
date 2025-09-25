package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

public class PedroCommands {
//    public Pose startPose = new Pose(9, 111, Math.toRadians(270));
//    public Pose scorePose = new Pose(50, 80, Point.CARTESIAN);
//
//    ArrayList<PathChain> paths = new ArrayList<>();
//    PathChain pathChain = new PathChain();
//    Path path = new Path(
//        new BezierLine(
//            new Point(startPose),
//            new Point(50, 80, Point.CARTESIAN)
//        )
//    );

//    public void generatePath() {
//        paths.add(
//            drive.follower.pathBuilder().addPath(path).build()
//        );
//    }

    public Command execute() {
//        generatePath();

//        return wait.noop().andThen(
//            new FollowPathCommand(drive.follower, paths.get(0)),
//            new FollowPathCommand(drive.follower, pathChain),
//            new FollowPathCommand(drive.follower, path)
//        );

        return new FollowPathCommand(
            drive.follower,
            new Path(
                new BezierLine(
                    new Pose(),
                    new Pose(10, 0, 0)
                )
            )
        );
    }
}