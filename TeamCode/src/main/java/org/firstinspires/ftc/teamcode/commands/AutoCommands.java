package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.TRANSPARENT;
import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.lights;
import static org.firstinspires.ftc.teamcode.commands.Commands.quanomous;
import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.RepeatCommand;
import org.firstinspires.ftc.teamcode.game.Side;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

import java.util.HashMap;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
            quanomous.execute(),
            auto.stop()
        ).withTimeout(29500).andThen(
            auto.stop()
        );
    }

    public Command delayStart() {
        return new DeferredCommand(
            () -> wait.seconds(config.delay), null
        );
    }

    public Command intakeStart() {
        return auto.goalLock(false).andThen(
            intake.forward(),
            conveyor.forward(),
            gate.close()
        );
    }

    public Command intakeStop() {
        return auto.goalLock(true).andThen(
            flywheel.forward(),
            conveyor.reverse(),
            gate.hold(),
            wait.doherty(2),
            conveyor.stop(),
            intake.hold()
        );
    }

    public Command intake(int spike) {
        return new SelectCommand(
            new HashMap<Object, Command>() {{
                put(0, drive.toSpike0());
                put(1, drive.toSpike1());
                put(2, drive.toSpike2());
                put(3, drive.toSpike3());
            }}, () -> spike
        ).alongWith(
            wait.doherty(2).andThen(
                auto.depositStop(),
                auto.intakeStart(),
                drive.untilDistance(TILE_WIDTH * -2),
                new DeferredCommand(() -> spike == 0 ? drive.setPowerSpike0() : drive.setPowerIntake(), null)
            )
        );
    }

    public Command depositStart() {
        return auto.goalLock(true).andThen(
            gate.open(),
            intake.forward(),
            flywheel.forward(),
            conveyor.launch()
        );
    }

    public Command depositStop() {
        return auto.goalLock(false).andThen(
            conveyor.stop(),
            flywheel.stop(),
            intake.reset(),
            intake.stop()
        );
    }

    public Command deposit(Side side, double axialOffset, double lateralOffset) {
        return auto.intakeStop().alongWith(
            drive.setPowerAuto(),
            new SelectCommand(
                new HashMap<Object, Command>() {{
                    put(NORTH, drive.toDepositNorth(axialOffset, lateralOffset));
                    put(SOUTH, drive.toDepositSouth(axialOffset, lateralOffset));
                }}, () -> side
            ).alongWith(
                drive.untilDistance(side == NORTH || config.pose.x < -2 * TILE_WIDTH ? -9 : -60).andThen(
                    drive.untilHeading(13),
                    side == NORTH ? drive.untilNotBusy() : wait.noop(),
                    side == NORTH ? drive.untilHeading(4).withTimeout(1000) : wait.noop(),
                    side == NORTH ? flywheel.isReady() : wait.noop(),
                    auto.depositStart(),
                    wait.doherty(2)
                )
            )
        );
    }

    public Command releaseGate() {
        return drive.toGate().alongWith(
            gate.close()
        );
    }

    public Command gateIntake() {
        return intakeStart().alongWith(
            drive.toGate().andThen(
                drive.setPowerHigh(),
                drive.toGateIntake(),
                wait.doherty(1),
                drive.setPowerLow(),
                drive.toGateIntakeDepart().withTimeout(400),
                drive.setPowerAuto()
            )
        );
    }

    public Command goalLock(boolean enabled) {
        return vision.goalLock(enabled).alongWith(
            drive.goalLock(enabled)
        );
    }

    public Command drive(Pose pose) {
        return drive.curve(pose).alongWith(
            auto.depositStop()
        );
    }

    public Command chase(int cycles) {
        return vision.chaseLock(true).alongWith(
            lights.set(TRANSPARENT),
            new RepeatCommand(
                execution -> drive.toChase(execution).alongWith(
                    drive.untilDistance(-1 * TILE_WIDTH).andThen(drive.setPowerLow()),
                    wait.milliseconds(50).andThen(vision.resetElement()),
                    auto.intakeStart()
                ).withTimeout(2000 + 200L * execution).andThen(
                    wait.doherty(),
                    drive.setPowerAuto(),
                    auto.deposit(NORTH, -0.25 * TILE_WIDTH, config.alliance.sign * -0.0 * TILE_WIDTH)
                ), cycles
            )
        );
    }

    public Command clusterChase(int cycles) {
        return new RepeatCommand(
            e1 -> new RepeatCommand(
                e2 -> drive.toChaseScan().alongWith(
                    intake.reset(),
                    vision.resetElement(),
                    vision.chaseLock(true),
                    vision.waitForElement().andThen(
                        auto.intakeStart(),
                        drive.stop(),
                        drive.chaseLock(true),
                        intake.waitForElement().withTimeout(3000),
                        vision.chaseLock(false)
                    )
                ), cycles
            ).andThen(
                vision.chaseLock(false),
                drive.chaseLock(false),
                auto.intakeStop(),
                auto.deposit(config.side, 0, 0)
            ), Integer.MAX_VALUE
        );
    }

    /** @noinspection unused*/
    public Command park(boolean gate, NavSubsystem.Axial axial, NavSubsystem.Lateral lateral) {
        return wait.until(() -> !config.goalLock).withTimeout(800).andThen(
            drive.setPowerAuto().alongWith(
                drive.toParking(config.parkGate, axial, lateral)
            ).andThen(
                stop()
            )
        );
    }

    public Command stop() {
        return drive.goalLock(false).alongWith(
            intake.stop(),
            conveyor.stop(),
            gate.close(),
            flywheel.stop()
        );
    }
}
