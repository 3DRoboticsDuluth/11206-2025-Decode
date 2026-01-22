package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.game.Config.config;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public class ConfigCommands {
    public Command setEditable(boolean editable) {
        return complete(
            () -> Subsystems.config.setEditable(editable)
        );
    }

    public Command changeItem(ConfigSubsystem.Change change) {
        return complete(
            () -> Subsystems.config.changeItem(change)
        );
    }

    public Command changeValue(ConfigSubsystem.Change change) {
        return complete(
            () -> Subsystems.config.changeValue(change)
        );
    }

    /** @noinspection unused*/
    public Command setInterrupt(boolean interrupt) {
        return complete(
            () -> config.interrupt = interrupt
        );
    }

    public Command complete(Runnable runnable) {
        return new SelectCommand(
            () -> new InstantCommand(runnable, Subsystems.config)
        );
    }
}
