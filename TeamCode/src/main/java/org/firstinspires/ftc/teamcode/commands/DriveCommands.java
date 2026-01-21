package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.toPedroPose;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_AUTO;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_HIGH;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_INTAKE;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_LOW;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_MEDIUM;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.TO_FAR;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.FuturePose;
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
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

/** @noinspection unused, UnusedReturnValue */
public class DriveCommands {
    public static double HEADING_END_TIME = 0.5;
    private boolean reverse = false;
    private Pose startPose = new Pose(0, 0, 0);
    private Pose endPose = new Pose(0, 0, 0);

    public Command input(DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn) {
        return new RunCommand(
            () -> drive.inputs(
                forward.getAsDouble(),
                strafe.getAsDouble(),
                turn.getAsDouble()
            ), drive
        );
    }

    public Command setPowerIntake() {
        return complete(
            () -> drive.follower.setMaxPower(POWER_INTAKE)
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

    public Command setPowerAuto() {
        return complete(
            () -> drive.follower.setMaxPower(POWER_AUTO)
        );
    }

    public Command startChasing() {
        return new InstantCommand(drive::startChasing, drive);
    }

    public Command stopChasing() {
        return new InstantCommand(drive::stopChasing, drive);
    }

    public Command toStart() {
        return to(nav.getStartPose());
    }

    public Command toSpike0Approach() {
        return to(nav.getSpike0Approach());
    }

    public Command toSpike0() {
        return new SelectCommand(
            () -> curve(
                config.pose.x > 1 ?
                    nav.getSpike0().axial(TILE_WIDTH * 1).lateral(TILE_WIDTH * 0.8 * config.alliance.sign) :
                    nav.getSpike0().axial(TILE_WIDTH * 3.5).lateral(TILE_WIDTH * 2.15 * config.alliance.sign),
                config.pose.x > 1 ?
                    nav.getSpike0().axial(TILE_WIDTH * -1).lateral(TILE_WIDTH * -0.2 * config.alliance.sign) :
                    nav.getSpike0().axial(TILE_WIDTH * -1.5).lateral(TILE_WIDTH * -0.5 * config.alliance.sign),
                nav.getSpike0().axial(TILE_WIDTH * 0.75).lateral(TILE_WIDTH * 0.1 * config.alliance.sign)
            ).alongWith(
                wait.doherty().andThen(
                    toDistance(-TILE_WIDTH),
                    setPowerLow()
                )
            ).andThen(
                setPowerAuto()
            )
        );
    }

    /*public Command toSpike0() {
        return new SelectCommand(
            () -> curve(
                config.pose.x > 1 ?
                    nav.getSpike0().axial(TILE_WIDTH * 1).lateral(TILE_WIDTH * 0.8 * config.alliance.sign) :
                    nav.getSpike0().axial(TILE_WIDTH * 3.5).lateral(TILE_WIDTH * 2.15 * config.alliance.sign),
                config.pose.x > 1 ?
                    nav.getSpike0().axial(TILE_WIDTH * -0.5).lateral(TILE_WIDTH * -0.2 * config.alliance.sign) :
                    nav.getSpike0().axial(TILE_WIDTH * -1.5).lateral(TILE_WIDTH * -0.5 * config.alliance.sign),
                nav.getSpike0()
            ).andThen(
                setPowerLow(),
                forward(TILE_WIDTH * 0.5),
                setPowerAuto()
            )
        );
    }*/

    public Command toSpike1() {
        return curve(
            config.pose.x > 1 ?
                nav.getSpike1().axial(TILE_WIDTH * -1.1) :
                nav.getSpike1().axial(TILE_WIDTH * -1.85).lateral(TILE_WIDTH * 0.35 * config.alliance.sign),
            nav.getSpike1().axial(TILE_WIDTH * 1.25)
        );
    }

    public Command toSpike2() {
        return curve(
            config.pose.x > 1 ?
                nav.getSpike2().axial(TILE_WIDTH * -1.1).lateral(TILE_WIDTH * -0.35 * config.alliance.sign) :
                nav.getSpike2().axial(TILE_WIDTH * -1.1).lateral(TILE_WIDTH * 0.35 * config.alliance.sign),
            nav.getSpike2().axial(TILE_WIDTH * 1.25)
        );
    }

    public Command toSpike3() {
        return curve(
            config.pose.x > 1 ?
                nav.getSpike3().axial(TILE_WIDTH * -1.1).lateral(TILE_WIDTH * -0.5 * config.alliance.sign) :
                nav.getSpike3(),
            nav.getSpike3().axial(TILE_WIDTH * 0.95)
        );
    }

    public Command toDepositSouth(double axialOffset, double lateralOffset) {
        return new SelectCommand(
            () -> curve(
                new Pose(config.pose.x, 0, 0),
                nav.getDepositSouthPose(axialOffset, lateralOffset)
            )
        );
    }

    public Command toDepositNorth(double axialOffset, double lateralOffset) {
        return curve(
            nav.getDepositNorthPose(axialOffset, lateralOffset)
        );
    }

    public Command toGate() {
        return curve(
             nav.getGatePose().axial(TILE_WIDTH * -1.75),
             nav.getGatePose().axial(TILE_WIDTH * 0.1)
        );
    }

    public Command toBase() {
        return curve(nav.getBasePose());
    }

    public Command toClosestArtifact() {
        return wait.noop(); // TODO: Add Chase
    }

    public Command stop() {
        return complete(
            () -> {
                drive.follower.startTeleOpDrive();
                drive.follower.setTeleOpDrive(0,0,0,0);
            }
        );
    }

    public Command goalLock(boolean enabled) {
        return complete(
            () -> config.goalLock =
                config.started && !config.robotCentric &&
                config.alliance != Alliance.UNKNOWN &&
                config.side != Side.UNKNOWN && enabled
        );
    }

    public Command toDistance(double distance) {
        return distance > 0 ?
            wait.until(() -> drive.follower.getDistanceTraveledOnPath() >= distance) :
            wait.until(() -> drive.follower.getDistanceRemaining() < -distance);
    }

    public Command toTValue(double t) {
        return t > 0 ?
            wait.until(() -> drive.follower.getCurrentTValue() >= t) :
            wait.until(() -> drive.follower.getCurrentTValue() < 1 + t);
    }

    public Command toHeading(double heading) {
        return wait.until(() -> abs(nav.getGoalHeadingRemaining()) < heading);
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
        return complete(
            () -> gamepad1.gamepad.rumble(intensity, intensity, (int)(seconds * 1000))
        );
    }

    public Command forward(double distance) {
        return new SelectCommand(
            () -> to(
                endPose.x + cos(endPose.heading) * distance,
                endPose.y + sin(endPose.heading) * distance,
                toDegrees(endPose.heading)
            )
        );
    }

    public Command strafe(double distance) {
        return new SelectCommand(
            () -> to(
                endPose.x + cos(endPose.heading + PI / 2) * distance,
                endPose.y + sin(endPose.heading + PI / 2) * distance,
                toDegrees(endPose.heading)
            )
        );
    }

    public Command turn(double heading) {
        return new SelectCommand(
            () -> to(
                endPose.turn(heading)
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
            () -> follow(builder -> {
                startPose = getPose();
                builder
                    .addPath(new BezierCurve(() -> toPedroPose(startPose), toPedroPose(startPose.midpoint(pose)), toPedroPose(pose)))
                    .setLinearHeadingInterpolation(startPose.heading, (endPose = pose).heading, HEADING_END_TIME);
                if (reverse) builder.setReversed();
            }, holdEnd)
        );
    }

    public Command curve(Pose... poses) {
        return new SelectCommand(
            () -> follow(builder -> {
                List<FuturePose> futurePoses = new ArrayList<>();
                futurePoses.add(toPedroPose(startPose = getPose()));
                for (Pose pose : poses)
                    futurePoses.add(toPedroPose(endPose = pose));
                if (futurePoses.size() < 3)
                    futurePoses.add(1, toPedroPose(startPose.midpoint(endPose)));
                builder
                    .addPath(new BezierCurve(futurePoses.toArray(new FuturePose[0])))
                    .setLinearHeadingInterpolation(startPose.heading, endPose.heading, HEADING_END_TIME);
                if (reverse) builder.setReversed();
            }, true)
        );
    }

    public Command curves(Pose... poses) {
        return new SelectCommand(
            () -> follow(builder -> {
                startPose = endPose = getPose();
                for (Pose pose : poses) {
                    builder
                        .addPath(new BezierCurve(toPedroPose(endPose), toPedroPose(endPose.midpoint(pose)), toPedroPose(pose)))
                        .setLinearHeadingInterpolation(endPose.heading, (endPose = pose).heading, HEADING_END_TIME);
                    if (reverse) builder.setReversed();
                }
            }, true)
        );
    }

    /** @noinspection unchecked*/
    public Command paths(Consumer<PathBuilder>... consumers) {
        return new SelectCommand(
            () -> follow(
                builder -> {
                    for (Consumer<PathBuilder> consumer : consumers)
                        consumer.accept(builder);
                }, true
            )
        );
    }

    public Command follow(Consumer<PathBuilder> consumer, boolean holdEnd) {
        PathBuilder pathBuilder = drive.follower.pathBuilder();
        consumer.accept(pathBuilder);
        PathChain pathChain = pathBuilder.build();
        return follow(pathChain, holdEnd);
    }

    public Command follow(PathChain pathChain, boolean holdEnd) {
        return controlsReset().andThen(
            new FollowPathCommand(drive.follower, pathChain, holdEnd)
        );
    }

    public Command forward() {
        return complete(() -> reverse = false);
    }

    public Command reverse() {
        return complete(() -> reverse = true);
    }

    public Command controlsReset() {
        return new SelectCommand(
            () -> complete(() -> drive.controlsReset = false)
        );
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }

    private Pose getPose() {
        return reverse ? config.pose.reverse() : config.pose;
    }

    private static boolean compare(Pose expected, Pose actual, boolean includeHeading) {
        double threshold = 1;
        return abs(expected.x - actual.x) < threshold &&
            abs(expected.y - actual.y) < threshold &&
            (!includeHeading || abs(expected.heading - actual.heading) < toRadians(threshold));
    }
}
