package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deposit;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

public class DepositCommands {
    public Command launch() {
        return complete(deposit::launch);
    }

    public Command stop() {
        return complete(deposit::stop);
    }

    private Command complete(Runnable runnable) {
        return new SelectCommand(
                () -> new InstantCommand(runnable, deposit)
        );
    }
}
