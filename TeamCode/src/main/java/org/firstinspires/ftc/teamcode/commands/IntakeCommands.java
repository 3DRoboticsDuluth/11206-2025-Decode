package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class IntakeCommands {
    public Command forward() {
        return complete(intake::forward);
    }

    public Command reverse() {
        return complete(intake::reverse);
    }

    public Command stop() {
        return complete(intake::stop);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, intake);
    }
}
