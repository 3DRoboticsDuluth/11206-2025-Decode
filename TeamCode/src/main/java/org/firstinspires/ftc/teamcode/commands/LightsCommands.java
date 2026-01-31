package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.lights;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color;

public class LightsCommands {
    public Command set(Color color) {
        return complete(() -> lights.set(color));
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, lights);
    }
}
