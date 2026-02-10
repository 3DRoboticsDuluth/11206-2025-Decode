package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;

import java.util.function.Function;

public class RepeatCommand extends CommandBase {
    private final Function<Integer, Command> function;
    private final int repeat;
    private Command command;
    private int executions = 0;

    public RepeatCommand(Function<Integer, Command> function, int repeat) {
        this.function = function;
        this.repeat = repeat;
    }

    @Override
    public void initialize() {
        this.command = function.apply(executions++);
        this.command.initialize();
    }

    @Override
    public void execute() {
        this.command.execute();
    }

    @Override
    public boolean isFinished() {
        if (this.command.isFinished() && executions < repeat)
            initialize();
        return this.command.isFinished();
    }
}
