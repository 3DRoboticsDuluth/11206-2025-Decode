package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deposit;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;
import com.seattlesolvers.solverslib.command.Subsystem;

import org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public class DeflectorCommands {
    public Command setPosition() {
        return completeDeflector(deflector::setPosition);
    }

    private Command completeDeflector(Runnable runnable) {
        return complete(runnable, 0.4);
    }

    private Command complete(Runnable runnable, double seconds) {
        return new SelectCommand(
            () -> new InstantCommand(runnable, deflector)
        ).andThen(
            wait.seconds(seconds)
        );
    }
}
