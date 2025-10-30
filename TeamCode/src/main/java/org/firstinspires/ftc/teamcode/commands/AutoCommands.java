package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
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
        );
    }
    
    public Command delayStart() {
        return new SelectCommand(
            () -> wait.seconds(config.delay)
        );
    }

    public Command prepareDeposit() {
        return new SelectCommand(
            () -> gate.close().alongWith(
                flywheel.start(),
                flywheel.forFlyWheelReady()
            )
        );
    }

    public Command deposit() {
        return new SelectCommand(
            () -> intake.forward().andThen(
                conveyor.forward(),
                gate.open(),
                deflector.compensateForDropOff()
            )
        );
    }

    public Command intakeArtifact() {
        return new SelectCommand(
            () -> intake.forward().alongWith(
                conveyor.forward(),
                gate.close()
            )
        );
    }

    public Command spitArtifact() {
        return new SelectCommand(
            () -> intake.reverse().alongWith(
                conveyor.reverse(),
                gate.close()
            )
        );
    }

    public Command autoArtifact() {
        return new SelectCommand(
            () -> drive.toClosestArtifact().alongWith(
                auto.intakeArtifact()
            )
        );
    }

    public Command depositNear() {
        return new SelectCommand(
            () -> drive.toDepositNear().alongWith(
                prepareDeposit()
                .andThen(
                    deposit()
                )
            )
        );
    }

    public Command depositFar() {
        return new SelectCommand(
            () -> drive.toDepositFar().alongWith(
                prepareDeposit()
            ).andThen(
                deposit()
            )
        );
    }

    public Command depositFromPose() {
        return new SelectCommand(
            () -> drive.toDepositAlign().alongWith(
                prepareDeposit()
            ).andThen(
                deposit()
            )
        );
    }

    public Command humanPlayerZone() {
        return new SelectCommand(
            () -> drive.toHumanPlayerZone().alongWith(
                gate.close()
            )
        );
    }
}
