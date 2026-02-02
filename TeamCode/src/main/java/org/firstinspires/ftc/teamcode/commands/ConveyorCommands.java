package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.conveyor;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class ConveyorCommands {
    public Command launch() {
        return complete(conveyor::launch);
    }

    public Command forward() {
        return complete(conveyor::forward);
    }

    public Command reverse() {
        return complete(conveyor::reverse);
    }

    public Command stop() {
        return complete(conveyor::stop);
    }

    public Command waitUntilStopped() {
        return wait.doherty().andThen(
            wait.until(conveyor::stopped)
        );
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
