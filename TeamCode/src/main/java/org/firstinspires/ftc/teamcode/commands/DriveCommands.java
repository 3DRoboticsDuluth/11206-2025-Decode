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
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import android.util.Log;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;

import java.util.function.DoubleSupplier;

/** @noinspection unused*/
public class DriveCommands {
    private Pose targetPose = new Pose(0, 0, 0);

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
        return complete(
            () -> drive.follower.setMaxPower(POWER_LOW)
        );
    }

    public Command setPowerMedium() {
        return complete(
            () -> drive.follower.setMaxPower(POWER_MEDIUM)
        );
    }

    public Command setPowerHigh() {
        return complete(
            () -> drive.follower.setMaxPower(POWER_HIGH)
        );
    }

    public Command toStart() {
        return to(nav.getStartPose());
    }

    public Command toSpike0() {
        return to(nav.getSpike0());
    }

    public Command toSpike1() {
        return to(nav.getSpike1());
    }

    public Command toSpike2() {
        return to(nav.getSpike2());
    }

    public Command toSpike3() {
        return to(nav.getSpike3());
    }

    public Command toLaunchNear() {
        return to(nav.getLaunchNearPose());
    }

    public Command toLaunchFar() {
        return to(nav.getLaunchFarPose());
    }

    public Command toLaunchAlign() {
        return to(nav.getLaunchAlignPose());
    }

    public Command toGate() {
        return to(nav.getGatePose());
    }

    public Command toBase() {
        return to(nav.getBasePose());
    }

    public Command toClosestArtifact() {
        return wait.noop(); // TODO: Add Chase
    }

    public Command enableTargetLock() {
        return complete(() -> drive.targetLockEnabled = true);
    }

    public Command disableTargetLock() {
        return complete(() -> drive.targetLockEnabled = false);
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
        return new SelectCommand(
            () -> to(
                targetPose.x + cos(targetPose.heading) * distance,
                targetPose.y + sin(targetPose.heading) * distance,
                toDegrees(targetPose.heading)
            )
        );
    }

    public Command strafe(double distance) {
        return new SelectCommand(
            () -> to(
                targetPose.x + cos(targetPose.heading + PI / 2) * distance,
                targetPose.y + sin(targetPose.heading + PI / 2) * distance,
                toDegrees(targetPose.heading)
            )
        );
    }

    public Command turn(double heading) {
        return new SelectCommand(
            () -> to(
                targetPose.x,
                targetPose.y,
                heading
            )
        );
    }

    public Command to(double x, double y, double heading) {
        return to(new Pose(x, y, toRadians(heading)), true);
    }

    public Command to(double x, double y, double heading, boolean holdEnd) {
        return to(new Pose(x, y, toRadians(heading)), holdEnd);
    }

    public Command to(Pose pose) {
        return to(pose, true);
    }

    public Command to(Pose pose, boolean holdEnd) {
        return new SelectCommand(
            () -> follow(
                drive.follower.pathBuilder()
                    .addPath(new BezierCurve(() -> toPedroPose(config.pose), toPedroPose(targetPose = pose)))
                    .setLinearHeadingInterpolation(config.pose.heading, pose.heading)
                    .build()
                , holdEnd
            )
        );
    }

    public Command follow(Consumer<PathBuilder> pathBuilderConsumer, boolean holdEnd) {
        PathBuilder pathBuilder = drive.follower.pathBuilder();
        pathBuilderConsumer.accept(pathBuilder);
        PathChain pathChain = pathBuilder.build();
        return follow(pathChain, holdEnd);
    }

    public Command follow(PathChain pathChain, boolean holdEnd) {
        return controlsReset().andThen(
            new FollowPathCommand(drive.follower, pathChain, holdEnd).andThen(
                new InstantCommand(() -> drive.follower.startTeleopDrive())
            )
        );
    }

    public Command controlsReset() {
        return new SelectCommand(
            () -> complete(() -> drive.controlsReset = false)
        );
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, drive);
    }

    private static boolean compare(Pose expected, Pose actual, boolean includeHeading) {
        double threshold = 1;
        return abs(expected.x - actual.x) < threshold &&
            abs(expected.y - actual.y) < threshold &&
            (!includeHeading || abs(expected.heading - actual.heading) < toRadians(threshold));
    }
}
