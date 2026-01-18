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
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

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
            gate.close(),
            flywheel.hold()
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

    public Command actionCancel() {
        return drive.goalLock(false).alongWith(
            auto.stop(),
            drive.setPowerLow()
        );
    }

    public Command intakeSpike(int spike) {
        return new SelectCommand(
            new HashMap<Object, Command>() {{
                put(0, drive.toSpike0());
                put(1, drive.toSpike1());
                put(2, drive.toSpike2());
                put(3, drive.toSpike3());
            }}, () -> spike
        ).alongWith(
             wait.doherty(2).andThen(
                 drive.toDistance(TILE_WIDTH * -1.5),
                 auto.intakeStart()
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

    public Command depositSouth(double axialOffset, double lateralOffset) {
        return auto.intakeStop().alongWith(
            drive.toDepositSouth(axialOffset, lateralOffset).andThen(
                drive.toDistance(-6),
                drive.toHeading(2),
                auto.deposit()
            )
        );
    }

    public Command depositNorth(double axialOffset, double lateralOffset) {
        return auto.intakeStop().alongWith(
            drive.toDepositNorth(axialOffset, lateralOffset).andThen(
                drive.toDistance(-6),
                drive.toHeading(2),
                auto.deposit()
            )
        );
    }

    public Command deposit() {
        return /*auto.fork(*/
            auto.depositStart().andThen(
                wait.doherty(2),
                auto.depositStop()
            )
        /*)*/;
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
        return drive.stop().alongWith(
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
