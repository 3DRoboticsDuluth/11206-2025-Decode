package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.light;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class LightCommands {
    public Command zero() {
        return complete(light::zero);
    }

    public Command one() {
        return complete(light::one);
    }

    public Command two() {
        return complete(light::two);
    }

    public Command three() {
        return complete(light::three);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, light);
    }
}
