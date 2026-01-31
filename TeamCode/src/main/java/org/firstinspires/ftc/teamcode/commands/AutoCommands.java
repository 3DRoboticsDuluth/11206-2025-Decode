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

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.game.Side;

import java.util.HashMap;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
             quanomous.execute()
        ).withTimeout(29500).andThen(
             auto.stop().asProxy()
        );
    }

    public Command delayStart() {
        return new SelectCommand(
            () -> wait.seconds(config.delay)
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
        return auto.goalLock(true).alongWith(
            flywheel.forward(),
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
            drive.untilPathCompletion(0.5).andThen(
                auto.intakeStart()
            )
        );
    }

    public Command depositStart() {
        return auto.goalLock(true).alongWith(
            intake.forward(),
            flywheel.forward(),
            conveyor.launch(),
            gate.open()
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
            new SelectCommand(
                new HashMap<Object, Command>() {{
                    put(NORTH, drive.toDepositNorth(axialOffset, lateralOffset));
                    put(SOUTH, drive.toDepositSouth(axialOffset, lateralOffset));
                }}, () -> side
            ).alongWith(
                drive.untilDistance(-6).andThen(
                    drive.untilHeading(2),
                    auto.depositStart().andThen(
                        wait.doherty(2),
                        auto.depositStop()
                    )
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

    public Command stop() {
        return drive.goalLock(false).alongWith(
            drive.setPowerLow(),
            drive.stop(),
            intake.stop(),
            conveyor.stop(),
            gate.close(),
            flywheel.stop()
        );
    }

    public Command fork(Command command) {
        return new InstantCommand(command::schedule);
    }
}
