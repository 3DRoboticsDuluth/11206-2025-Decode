package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class DeflectorCommands {
    public Command up() {
        return complete(deflector::up);
    }

    public Command down() {
        return complete(deflector::down);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, deflector);
    }
}
