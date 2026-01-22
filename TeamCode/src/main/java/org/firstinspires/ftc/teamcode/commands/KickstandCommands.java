package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.kickstand;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class KickstandCommands {
    public Command engage() {
        return complete(kickstand::engage);
    }

    public Command disengage() {
        return complete(kickstand::disengage);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
