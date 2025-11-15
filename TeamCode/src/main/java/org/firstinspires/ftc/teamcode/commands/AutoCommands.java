package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SelectCommand;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
            Commands.config.setInterrupt(true),
            drive.toLaunchNear(),
            wait.forInterruptA(),
            drive.toSpike3(),
            wait.forInterruptA(),
            drive.toSpike2(),
            wait.forInterruptA(),
            drive.toSpike1(),
            wait.forInterruptA(),
            drive.toSpike0(),
            wait.forInterruptA(),
            drive.toLaunchFar(),
            wait.forInterruptA(),
            drive.toGate(),
            wait.forInterruptA(),
            drive.toBase()
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

    public Command intakeSpike0() {
        // TODO: Reuse
        return drive.toSpike0().andThen(
            intakeStart(),
            drive.forward(16)
        );
    }

    public Command intakeSpike1() {
        // TODO: Reuse
        return drive.toSpike1().andThen(
            intakeStart(),
            drive.forward(16)
        );
    }

    public Command intakeSpike2() {
        // TODO: Reuse
        return drive.toSpike2().andThen(
            auto.intakeStart(),
            drive.forward(16)
        );
    }

    public Command intakeSpike3() {
        // TODO: Reuse
        return drive.toSpike3().andThen(
            auto.intakeStart(),
            drive.forward(16)
        );
    }

    public Command depositStart() {
        return drive.goalLock(true).andThen(
            intake.forward(),
            flywheel.forward(),
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

    public Command depositNear() {
        return drive.toLaunchNear().andThen(
            auto.deposit()
        );
    }

    public Command depositFar() {
        return drive.toLaunchFar().andThen(
            auto.deposit()
        );
    }

    public Command deposit() {
        return auto.depositStart().andThen(
            wait.doherty(3),
            auto.depositStop()
        );
    }

    public Command releaseGate() {
        return drive.toGate().andThen(
            drive.forward(8).withTimeout(1500),
            wait.seconds(1),
            drive.toGate()
        );
    }
}
