package org.firstinspires.ftc.teamcode.commands;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.RepeatCommand;

public class RepeatCommands {
    public Command times(Command command, int count) {
        return new RepeatCommand(command, count);
    }

    public Command continually(Command command) {
        return new RepeatCommand(command);
    }
}
