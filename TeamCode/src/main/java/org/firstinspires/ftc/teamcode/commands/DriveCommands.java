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
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_SPIKE0;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.TO_FAR;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.follower;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
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
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.ChaseCommand;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

/** @noinspection unused, UnusedReturnValue */
public class DriveCommands {
    public static double HEADING_END_TIME = 0.33;
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
            () -> follower.setMaxPower(POWER_INTAKE)
        );
    }

    public Command setPowerSpike0() {
        return complete(
             () -> follower.setMaxPower(POWER_SPIKE0)
        );
    }

    public Command setPowerLow() {
        return complete(
            () -> follower.setMaxPower(POWER_LOW)
        );
    }

    public Command setPowerMedium() {
        return complete(
            () -> follower.setMaxPower(POWER_MEDIUM)
        );
    }

    public Command setPowerHigh() {
        return complete(
            () -> follower.setMaxPower(POWER_HIGH)
        );
    }

    public Command setPowerAuto() {
        return complete(
            () -> follower.setMaxPower(POWER_AUTO)
        );
    }

    public Command toStart() {
        return to(nav.getStartPose());
    }

    public Command toSpike0() {
        return new DeferredCommand(
            () -> curve(
                config.pose.x > 1 ?
                    nav.getSpike0().axial(TILE_WIDTH * 1).lateral(TILE_WIDTH * 0.8 * config.alliance.sign) :
                    nav.getSpike0().axial(TILE_WIDTH * -3.25).lateral(TILE_WIDTH * 2.15 * config.alliance.sign),
                config.pose.x > 1 ?
                    nav.getSpike0().axial(TILE_WIDTH * -1).lateral(TILE_WIDTH * -0.2 * config.alliance.sign) :
                    nav.getSpike0().axial(TILE_WIDTH * -1.5).lateral(TILE_WIDTH * -0.5 * config.alliance.sign),
                nav.getSpike0().axial(TILE_WIDTH * 0.7).hold(false)
            ), null
        );
    }

    public Command toSpike1() {
        return new DeferredCommand(
            () -> curve(
                config.pose.x > 1 ?
                    nav.getSpike1().axial(TILE_WIDTH * -1.1).axial(abs(config.pose.y) > 2 * TILE_WIDTH ? -0.75 * TILE_WIDTH : 0).lateral(TILE_WIDTH * -0.3 * config.alliance.sign) :
                    nav.getSpike1().axial(TILE_WIDTH * -1.85).axial(abs(config.pose.y) > 2 * TILE_WIDTH ? -0.75 * TILE_WIDTH : 0).lateral(TILE_WIDTH * 0.3 * config.alliance.sign),
                nav.getSpike1().axial(TILE_WIDTH * 1.5).hold(false)
            ), null
        );
    }

    public Command toSpike2() {
        return new DeferredCommand(
            () -> curve(
                config.pose.x > 1 ?
                    nav.getSpike2().axial(TILE_WIDTH * -1.1).axial(abs(config.pose.y) > 2 * TILE_WIDTH ? -1 * TILE_WIDTH : 0).lateral(TILE_WIDTH * -0.5 * config.alliance.sign) :
                    nav.getSpike2().axial(TILE_WIDTH * -1.1).axial(abs(config.pose.y) > 2 * TILE_WIDTH ? -1 * TILE_WIDTH : 0).lateral(TILE_WIDTH * 0 * config.alliance.sign),
                nav.getSpike2().axial(TILE_WIDTH * 1.5).hold(false)
            ), null
        );
    }

    public Command toSpike3() {
        return new DeferredCommand(
            () -> curve(
                config.pose.x > 1 ?
                    nav.getSpike3().axial(TILE_WIDTH * -1.5).axial(abs(config.pose.y) > 2 * TILE_WIDTH ? -0.75 * TILE_WIDTH : 0).lateral(TILE_WIDTH * -0.5 * config.alliance.sign) :
                    nav.getSpike3().axial(TILE_WIDTH * -1.5).axial(abs(config.pose.y) > 2 * TILE_WIDTH ? -0.75 * TILE_WIDTH : 0).lateral(TILE_WIDTH * 0.5 * config.alliance.sign),
                nav.getSpike3().axial(TILE_WIDTH * 1.2).hold(false)
            ), null
        );
    }

    public Command toDepositSouth(double axialOffset, double lateralOffset) {
        return new DeferredCommand(
            () -> curve(
                config.pose.x > -TILE_WIDTH ?
                    new Pose(config.pose.x, config.alliance.sign * -0.5, 0) :
                    config.pose.midpoint(nav.getDepositSouthPose(axialOffset, lateralOffset)),
                nav.getDepositSouthPose(axialOffset, lateralOffset).hold(false)
            ), null
        );
    }

    public Command toDepositNorth(double axialOffset, double lateralOffset) {
        return curve(
            nav.getDepositNorthPose(axialOffset, lateralOffset)
        ).alongWith(
            untilDistance(-1 * TILE_WIDTH).andThen(setPowerMedium())
        ).andThen(
            setPowerHigh()
        );
    }

    public Command toGate() {
        return curve(
             nav.getGatePose().axial(TILE_WIDTH * -1.75),
             nav.getGatePose().axial(TILE_WIDTH * 0.1).hold(false)
        );
    }

    public Command toGateIntake() {
        return curve(
            nav.getGateIntakePose().axial(TILE_WIDTH * -.1).lateral(config.alliance.sign * TILE_WIDTH * -0.3),
            nav.getGateIntakePose().hold(true) // TODO: Test
        );
    }

    public Command toGateIntakeDepart() {
        return curve(
            nav.getGateIntakeDepartPose().axial(TILE_WIDTH * config.side.sign * -0.05).lateral(TILE_WIDTH * -.1),
            nav.getGateIntakeDepartPose().hold(false)
        );
    }

    public Command toBase() {
        return curve(nav.getBasePose());
    }

    public Command toParking(boolean gate, NavSubsystem.Axial axial, NavSubsystem.Lateral lateral) {
        return curve(
            nav.getParkingPose(gate, axial, lateral)
        );
    }

    public Command hold() {
        return complete(
            () -> follower.holdPoint(
                toPedroPose(config.pose.hold(true))
            )
        );
    }

    public Command stop() {
        return complete(
            () -> {
                follower.startTeleOpDrive();
                follower.setTeleOpDrive(0,0,0,0);
            }
        );
    }

    public Command goalLock(boolean enabled) {
        return complete(
            () -> drive.setGoalLock(enabled)
        );
    }

    public Command chaseLock(boolean enabled) {
        return complete(
            () -> drive.setChaseLock(enabled)
        );
    }

    public Command toChaseScan() {
        return new DeferredCommand(
            () -> curve(nav.getChaseScanPose()), null
        );
    }

    public Command toChase(int execution) {
        return new DeferredCommand(
            () -> curve(
                nav.getChasePose(execution).axial(-1.25 * TILE_WIDTH),
                nav.getChasePose(execution)
            ), null
        );
    }

    public Command chase() {
        return new ChaseCommand(() -> vision.element);
    }

    public Command untilDistance(double distance) {
        return wait.doherty().andThen(
            distance > 0 ?
                wait.until(() -> follower.getDistanceTraveledOnPath() >= distance) :
                wait.until(() -> follower.getDistanceRemaining() < -distance)
        );
    }

    public Command untilPathCompletion(double percentage) {
        return wait.doherty().andThen(
            percentage > 0 ?
                wait.until(() -> follower.getPathCompletion() >= percentage) :
                wait.until(() -> follower.getPathCompletion() < 1 + percentage)
        );
    }

    public Command untilTValue(double t) {
        return wait.doherty().andThen(
            t > 0 ?
                wait.until(() -> follower.getCurrentTValue() >= t) :
                wait.until(() -> follower.getCurrentTValue() < 1 + t)
        );
    }

    public Command untilHeading(double heading) {
        return wait.until(() -> abs(nav.getGoalHeadingRemaining()) < toRadians(heading));
    }

    public Command untilNotBusy() {
        return complete(
            () -> wait.until(() -> !drive.isBusy())
        );
    }

    public boolean isToFar(Pose pose) {
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
        return new DeferredCommand(
            () -> to(
                endPose.x + cos(endPose.heading) * distance,
                endPose.y + sin(endPose.heading) * distance,
                toDegrees(endPose.heading)
            ), null
        );
    }

    public Command strafe(double distance) {
        return new DeferredCommand(
            () -> to(
                endPose.x + cos(endPose.heading + PI / 2) * distance,
                endPose.y + sin(endPose.heading + PI / 2) * distance,
                toDegrees(endPose.heading)
            ), null
        );
    }

    public Command turn(double heading) {
        return new DeferredCommand(
            () -> to(
                endPose.turn(heading)
            ), null
        );
    }

    public Command to(double x, double y, double heading) {
        return to(new Pose(x, y, toRadians(heading)));
    }

    public Command to(double x, double y, double heading, boolean holdEnd) {
        return to(new Pose(x, y, toRadians(heading), holdEnd));
    }

    public Command to(Pose pose) {
        return new DeferredCommand(
            () -> follow(builder -> {
                startPose = getPose();
                builder
                    .addPath(new BezierCurve(() -> toPedroPose(startPose), toPedroPose(startPose.midpoint(pose)), toPedroPose(pose)))
                    .setLinearHeadingInterpolation(startPose.heading, (endPose = pose).heading, HEADING_END_TIME);
                if (reverse) builder.setReversed();
            }, pose.hold), null
        );
    }

    public Command curve(Pose... poses) {
        return new DeferredCommand(
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
            }, poses.length > 0 && poses[poses.length - 1].hold), null
        );
    }

    public Command curves(Pose... poses) {
        return new DeferredCommand(
            () -> follow(builder -> {
                startPose = endPose = getPose();
                for (Pose pose : poses) {
                    builder
                        .addPath(new BezierCurve(toPedroPose(endPose), toPedroPose(endPose.midpoint(pose)), toPedroPose(pose)))
                        .setLinearHeadingInterpolation(endPose.heading, (endPose = pose).heading, HEADING_END_TIME);
                    if (reverse) builder.setReversed();
                }
            }, poses.length > 0 && poses[poses.length - 1].hold), null
        );
    }

    /** @noinspection unchecked*/
    public Command paths(Consumer<PathBuilder>... consumers) {
        return new DeferredCommand(
            () -> follow(
                builder -> {
                    for (Consumer<PathBuilder> consumer : consumers)
                        consumer.accept(builder);
                }, false
            ), null
        );
    }

    public Command follow(Consumer<PathBuilder> consumer, boolean holdEnd) {
        PathBuilder pathBuilder = follower.pathBuilder();
        consumer.accept(pathBuilder);
        PathChain pathChain = pathBuilder.build();
        return follow(pathChain, holdEnd);
    }

    public Command follow(PathChain pathChain, boolean holdEnd) {
        return controlsReset().andThen(
            new FollowPathCommand(follower, pathChain, holdEnd)
        );
    }

    public Command forward() {
        return complete(() -> reverse = false);
    }

    public Command reverse() {
        return complete(() -> reverse = true);
    }

    public Command controlsReset() {
        return complete(() -> drive.controlsReset = false);
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
