package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.sorting;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class SortingCommands {
    public Command sort() {
        return complete(sorting::sort);
    }

    public Command pass() {
        return complete(sorting::pass);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, sorting);
    }
}
