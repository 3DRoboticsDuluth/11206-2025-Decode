package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.toPedroPose;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_HIGH;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_LOW;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_MEDIUM;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.TO_FAR;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;

import java.util.function.DoubleSupplier;

@SuppressWarnings({"unused"})
public class DriveCommands {
    public Command input(DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn) {
        return new RunCommand(
            () -> drive.inputs(
                forward.getAsDouble(),
                strafe.getAsDouble(),
                turn.getAsDouble()
            ), drive
        );
    }

    public Command setPowerLow() {
        return new InstantCommand(() -> DriveSubsystem.POWER = POWER_LOW);
    }

    public Command setPowerMedium() {
        return new InstantCommand(() -> DriveSubsystem.POWER = POWER_MEDIUM);
    }

    public Command setPowerHigh() {
        return new InstantCommand(() -> DriveSubsystem.POWER = POWER_HIGH);
    }

    public Command toStart() {
        return to(nav.getStartPose(), true);
    }
    public Command toSpike0(){return to(nav.getSpike0(), true);}
    public Command toSpike1(){return to(nav.getSpike1(), true);}
    public Command toSpike2(){return to(nav.getSpike2(), true);}
    public Command toSpike3(){return to(nav.getSpike3(), true);}
    public Command toLaunchNear() {return to(nav.getLaunchNearPose(), true);}
    public Command toLaunchFar() {
        return to(nav.getLaunchFarPose(), true);
    }
    public Command toLaunchAlign() {
        return to(nav.getLaunchAlignPose(), true);
    }
    public Command toGetPose(){return to(nav.getGatePose(), true);}
    public Command toBasePose(){return to(nav.getBasePose(), true);}
    public Command toClosestArtifact() {
        return wait.noop(); // TODO: Add Chase
    }

    public Command toDistance(double distance) {
        return distance > 0 ?
            wait.until(() -> drive.follower.getDistanceTraveledOnPath() >= distance) :
            wait.until(() -> drive.follower.getDistanceRemaining() < -distance);
    }

    public boolean toFar(Pose pose) {
        return config.teleop &&
            config.pose != null &&
            pose != null &&
            abs(pose.hypot(config.pose)) > TO_FAR;
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

    public Command forward(double distance) {
        double heading = config.pose.heading;
        return follow(
            pb -> pb.addPath(new BezierCurve(
                new com.pedropathing.geometry.Pose(config.pose.x, config.pose.y),
                new com.pedropathing.geometry.Pose(
                    config.pose.x + cos(heading) * distance,
                    config.pose.y + sin(heading) * distance
                )
            )).setLinearHeadingInterpolation(config.pose.heading, config.pose.heading),
            true
        );
    }

    public Command strafe(double distance) {
        double heading = config.pose.heading;
        double bearing = heading + Math.toRadians(90);
        return follow(
            pb -> pb.addPath(new BezierCurve(
                new com.pedropathing.geometry.Pose(config.pose.x, config.pose.y),
                new com.pedropathing.geometry.Pose(
                    config.pose.x + cos(bearing) * distance,
                    config.pose.y + sin(bearing) * distance
                )
            )).setLinearHeadingInterpolation(config.pose.heading, config.pose.heading),
            true
        );
    }

    public Command turn(double heading) {
        return follow(
            pb -> pb.setConstantHeadingInterpolation(config.pose.heading + Math.toRadians(heading)),
            true
        );
    }

    public Command to(Pose pose, boolean holdEnd) {
        return follow(
            drive.follower.pathBuilder()
                .addPath(new BezierLine(() -> toPedroPose(config.pose), toPedroPose(pose)))
                .setLinearHeadingInterpolation(config.pose.heading, pose.heading)
                .build()
            , holdEnd
        );
    }

    public Command follow(Consumer<PathBuilder> pathBuilderConsumer, boolean holdEnd) {
        PathBuilder pathBuilder = drive.follower.pathBuilder();
        pathBuilderConsumer.accept(pathBuilder);
        PathChain pathChain = pathBuilder.build();
        return follow(pathChain, holdEnd);
    }

    public Command follow(PathChain pathChain, boolean holdEnd) {
        return new FollowPathCommand(drive.follower, pathChain, holdEnd);
    }

    private static boolean compare(Pose expected, Pose actual, boolean includeHeading) {
        double threshold = 1;
        return abs(expected.x - actual.x) < threshold &&
            abs(expected.y - actual.y) < threshold &&
            (!includeHeading || abs(expected.heading - actual.heading) < toRadians(threshold));
    }
}
