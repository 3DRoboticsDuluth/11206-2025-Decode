package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.adaptations.pedropathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.constants.LConstants;

import java.util.ArrayList;

@Autonomous(name = "FollowPedroSample")
public class FollowPedroSample extends CommandOpMode {
    Follower follower;
    ArrayList<PathChain> paths = new ArrayList<>();
    PathChain pathChain = new PathChain();
    Path path = new Path(
        new BezierCurve(
            new Point(20, 30, Point.CARTESIAN),
            new Point(50, 80, Point.CARTESIAN)
        )
    );

    public void generatePath() {
        paths.add(
            follower.pathBuilder().addPath(path).build()
        );
    }

    @Override
    public void initialize() {
        super.reset();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(new Pose());

        generatePath();

        schedule(
            new RunCommand(() -> {
                follower.update();
                telemetry.addData("Message", "Let's go!");
                telemetry.update();
            }),
//            new FollowPathCommand(
//                follower,
//                new Path(
//                    new BezierLine(
//                        new Pose(),
//                        new Pose(10, 0, 0)
//                    )
//                )
//            )
            new FollowPathCommand(follower, paths.get(0))
//            new FollowPathCommand(follower, pathChain),
//            new FollowPathCommand(follower, path)
        );
    }
}