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
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
            quanomous.execute()
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
        return drive.toSpike0().alongWith(
            auto.intakeStart(),
            drive.toDistance(-24).andThen(
                drive.setPowerIntake()
            )
        ).andThen(
            drive.setPowerAuto(),
            auto.intakeStop()
        );
    }

    public Command intakeSpike1() {
        // TODO: Reuse
        return drive.toSpike1().andThen(
            auto.intakeStart(),
            drive.setPowerIntake(),
            drive.forward(19).withTimeout(3000),
            drive.setPowerAuto(),
            auto.intakeStop()
        );
    }

    public Command intakeSpike2() {
        // TODO: Reuse
        return drive.toSpike2().andThen(
            auto.intakeStart(),
            drive.setPowerIntake(),
            drive.forward(19).withTimeout(3000),
            drive.setPowerAuto(),
            auto.intakeStop()
        );
    }

    public Command intakeSpike3() {
        // TODO: Reuse
        return drive.toSpike3().andThen(
            auto.intakeStart(),
            drive.setPowerIntake(),
            drive.forward(19).withTimeout(3000),
            drive.setPowerAuto(),
            auto.intakeStop()
        );
    }

    public Command depositStart() {
        return drive.goalLock(true).andThen(
            intake.forward(),
            flywheel.forward(),
            wait.doherty(config.auto ? 2 : 0), //ReImplementing Gate
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
        return drive.toLaunchNear().alongWith(
            wait.doherty(2).andThen(
                auto.depositStart(),
                auto.deposit()
            )
        );
    }

    public Command depositFar() {
        return drive.toLaunchFar().alongWith(
            wait.doherty(2).andThen(
                auto.depositStart(),
                auto.deposit()
            )
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

    public Command goalLock(boolean enabled) {
        return new InstantCommand(() -> config.goalLock = enabled);
    }
}
