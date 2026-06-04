package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;

import java.util.function.Function;

public class RepeatCommand extends CommandBase {
    private final Function<Integer, Command> function;
    private final Function<Integer, Boolean> condition;
    private Command command;
    private int executions = 0;

    public RepeatCommand(Function<Integer, Command> function, int repeat) {
        this(function, executions -> executions >= repeat);
    }

    public RepeatCommand(Function<Integer, Command> function, Function<Integer, Boolean> condition) {
        this.function = function;
        this.condition = condition;
    }

    @Override
    public void initialize() {
        this.executions = 0;
        initializeCommand();
    }

    private void initializeCommand() {
        this.command = function.apply(executions++);
        this.command.initialize();
    }

    @Override
    public void execute() {
        this.command.execute();
    }

    @Override
    public boolean isFinished() {
        if (this.command.isFinished() && !condition.apply(executions)) {
            this.command.end(false);
            initializeCommand();
        }
        return this.command.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
        this.command.end(interrupted);
    }
}
