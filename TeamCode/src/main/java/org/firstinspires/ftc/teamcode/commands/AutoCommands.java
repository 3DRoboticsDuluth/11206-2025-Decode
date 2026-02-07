package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
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

import org.firstinspires.ftc.teamcode.game.Side;

import java.util.HashMap;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
            quanomous.execute(),
            wait.doherty(2)
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
        return auto.goalLock(false).alongWith(
            intake.forward(),
            conveyor.forward(),
            gate.close()
        );
    }

    public Command intakeStop() {
        return auto.goalLock(true).andThen(
            flywheel.forward(),
            conveyor.reverse(),
            gate.open(),
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
                auto.intakeStart(),
                drive.untilDistance(TILE_WIDTH * -1.5),
                drive.setPowerIntake()
            )
        );
    }

    public Command depositStart() {
        return auto.goalLock(true).andThen(
            intake.forward(),
            flywheel.forward(),
            conveyor.launch()
        );
    }

    public Command depositStop() {
        return auto.goalLock(false).alongWith(
            conveyor.stop(),
            flywheel.stop(),
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
                drive.untilDistance(side == NORTH  ? -12 : -30).andThen(
                    drive.untilHeading(10),
                    conveyor.waitUntilStopped(),
                    // TODO: Add flywheel.isReady() for NORTH?
                    auto.depositStart(),
                    wait.doherty(2),
                    auto.depositStop()
                )
            )
        );
    }

    public Command releaseGate() {
        return drive.toGate();
    }

    public Command goalLock(boolean enabled) {
        return drive.goalLock(enabled).alongWith(
            vision.goalLock(enabled)
        );
    }
    public Command artifactLock(boolean enabled) {
        return drive.artifactLock(enabled).alongWith(
            vision.artifactLock(enabled)
        );
    }

    public Command chase(int cycles) {
        return auto.intakeStart();
    }

    public Command stop() {
        return drive.stop().alongWith(
            intake.stop(),
            conveyor.stop(),
            gate.close(),
            flywheel.stop()
        );
    }
}