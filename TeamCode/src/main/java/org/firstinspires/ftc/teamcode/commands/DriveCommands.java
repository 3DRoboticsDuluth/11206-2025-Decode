package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_HIGH;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_LOW;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_MEDIUM;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.TO_FAR;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Pose;
import org.firstinspires.ftc.teamcode.game.Side;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

/** @noinspection DataFlowIssue*/
@SuppressWarnings({"unused"})
public class DriveCommands {
//    private final HashMap<Location, Function<Pose, Pose>> locationPoses =
//        new HashMap<Location, Function<Pose, Pose>>() {{
//        }};

    PathBuilder pathBuilder = new PathBuilder();

    public Command setPowerLow() {
        return new InstantCommand(() -> drive.power = POWER_LOW);
    }

    public Command setPowerMedium() {
        return new InstantCommand(() -> drive.power = POWER_MEDIUM);
    }

    public Command setPowerHigh() {
        return new InstantCommand(() -> drive.power = POWER_HIGH);
    }

    public Command input(DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn) {
        return new RunCommand(
            () -> drive.inputs(
                forward.getAsDouble(),
                strafe.getAsDouble(),
                turn.getAsDouble()
            ), drive
        );
    }

    public Command toStart() {
        return to(nav.getStartPose());
    }

    public Command toNearAprilTag() {
        return new SelectCommand(
            () -> to(
                nav.createPose(
                    signum(config.pose.x) * TILE_WIDTH * 2,
                    signum(config.pose.y) * TILE_WIDTH * 2,
                    toRadians(90 - signum(config.pose.x) * 90)
                )
            )
        );
    }

//    public Command to(Location location, Position position) {
//        return to(locationPoses.get(location).apply(
//            location.offsets.get(position)
//        ));
//    }
//
    public Command to(double x, double y, double heading) {
        return to(
            nav.createPose(x, y, heading)
        );
    }
    
    public boolean toFar(Pose pose) {
        return config.teleop &&
            config.pose != null &&
            pose != null &&
            abs(pose.hypot(config.pose)) > TO_FAR;
    }
    
    public Command shake() {
        return rumble().andThen(
            turn(-10),
            turn(10)
        );
    }

    public Command rumble() {
        return rumble1().alongWith(rumble2());
    }

    public Command rumble1() {
        return rumble(gamepad1, 1, 1);
    }

    public Command rumble2() {
        return rumble(gamepad1, 1, 1);
    }

    public Command rumble1(double intensity, double seconds) {
        return rumble(gamepad1, intensity, seconds);
    }

    public Command rumble2(double intensity, double seconds) {
        return rumble(gamepad1, intensity, seconds);
    }

    public Command rumble(GamepadEx gamepad, double intensity, double seconds) {
        return new InstantCommand(
            () -> gamepad1.gamepad.rumble(intensity, intensity, (int)(seconds * 1000))
        );
    }

    public Command to(Pose pose) {
        return new SelectCommand(() ->
            pose == null || compare(config.pose, pose, true) ?
                toFar(pose) || config.alliance == Alliance.UNKNOWN || config.side == Side.UNKNOWN ?
                    shake() : wait.noop() : (
                compare(config.pose, pose, false) ?
                    turn(toDegrees(pose.heading - config.pose.heading)) :
                    follow(pb -> pb.addPath(new BezierCurve(
                        new Point(config.pose.x, config.pose.y),
                        new Point(pose.x, pose.y)
                    )).setLinearHeadingInterpolation(config.pose.heading, pose.heading))
            )
        );
    }

    public Command forward(double distance) {
        double heading = config.pose.heading;
        return follow(
            pb -> pb.addPath(new BezierCurve(
                new Point(config.pose.x, config.pose.y),
                new Point(
                    config.pose.x + cos(heading) * distance,
                    config.pose.y + sin(heading) * distance
                )
            )).setLinearHeadingInterpolation(config.pose.heading, config.pose.heading)
        );
    }

    public Command strafe(double distance) {
        double heading = config.pose.heading;
        double bearing = heading + Math.toRadians(90);
        return follow(
            pb -> pb.addPath(new BezierCurve(
                new Point(config.pose.x, config.pose.y),
                new Point(
                    config.pose.x + cos(bearing) * distance,
                    config.pose.y + sin(bearing) * distance
                )
            )).setLinearHeadingInterpolation(config.pose.heading, config.pose.heading)
        );
    }

    public Command turn(double heading) {
        return follow(
            pb -> pb.setConstantHeadingInterpolation(config.pose.heading + Math.toRadians(heading))
        );
    }

    public Command follow(Consumer<PathBuilder> pathBuilderConsumer) {
        PathBuilder pathBuilder = drive.follower.pathBuilder();
        pathBuilderConsumer.accept(pathBuilder);
        PathChain pathChain = pathBuilder.build();
        return new FollowPathCommand(drive.follower, pathChain, true);
    }

    private static boolean compare(Pose expected, Pose actual, boolean includeHeading) {
        double threshold = 1;
        return abs(expected.x - actual.x) < threshold &&
            abs(expected.y - actual.y) < threshold &&
            (!includeHeading || abs(expected.heading - actual.heading) < toRadians(threshold));
    }
}
