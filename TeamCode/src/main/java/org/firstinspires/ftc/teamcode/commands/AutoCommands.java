package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.quanomous;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;

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
        return drive.goalLock(false).alongWith(
            intake.forward(),
            conveyor.forward(),
            gate.close(),
            flywheel.hold()
        );
    }

    public Command intakeStop() {
        return conveyor.reverse().andThen(
            wait.doherty(2),
            conveyor.stop(),
            intake.hold(),
            gate.open()
        ).alongWith(
            wait.doherty(2).andThen(
                drive.goalLock(true).andThen(
                    flywheel.forward(),
                    flywheel.isReady(),
                    drive.rumble()
                )
            )
        );
    }

    public Command intake(double distance) {
        return auto.intakeStart().andThen(
            drive.forward(distance).withTimeout(1500),
            drive.setPowerAuto()
        );
    }

    public Command intakeSpike(int spike) {
        return new SelectCommand(
            new HashMap<Object, Command>() {{
                put(1, drive.toSpike1());
                put(2, drive.toSpike2());
                put(3, drive.toSpike3());
            }}, () -> spike
        ).andThen(
            drive.setPowerIntake(),
            auto.intake(22)
        );
    }

    public Command intakeSpike0() {
        return drive.toSpike0Approach().andThen(
            drive.toSpike0(),
            drive.setPowerMedium(),
            drive.turn(config.alliance.sign * 25).withTimeout(500),
            auto.intake(7).withTimeout(750),
            drive.turn(config.alliance.sign * 5).withTimeout(500),
            auto.intake(16.7)
        );
    }

    public Command depositStart() {
        return drive.goalLock(true).andThen(
            intake.forward(),
            flywheel.forward(),
            wait.doherty(config.auto ? 2 : 0),
            flywheel.isReady(),
            conveyor.launch()
        );
    }

    public Command depositStop() {
        return drive.goalLock(false).alongWith(
            conveyor.stop(),
            flywheel.stop(),
            intake.stop()
        );
    }

    public Command depositSouth(double axialOffset, double lateralOffset) {
        return drive.toDepositSouth(axialOffset, lateralOffset).alongWith(
            auto.intakeStop(),
            auto.deposit()
        );
    }

    public Command depositNorth(double axialOffset, double lateralOffset) {
        return drive.toDepositNorth(axialOffset, lateralOffset).alongWith(
            auto.intakeStop(),
            auto.deposit()
        );
    }

    public Command deposit() {
        return wait.doherty(4).andThen(
            auto.depositStart(),
            wait.doherty(2),
            auto.depositStop()
        );
    }

    public Command releaseGate() {
        return drive.toGate().andThen(
            drive.forward(32).withTimeout(3000),
            wait.seconds(1),
            drive.toGate()
        );
    }

    public Command goalLock(boolean enabled) {
        return new InstantCommand(() -> config.goalLock = enabled);
    }

    public Command stop() {
        return drive.stop().alongWith(
            intake.stop(),
            conveyor.stop(),
            flywheel.stop()
        );
    }
}
