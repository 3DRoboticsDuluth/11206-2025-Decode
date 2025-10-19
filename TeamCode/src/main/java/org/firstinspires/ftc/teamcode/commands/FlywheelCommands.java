package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class FlywheelCommands {
    public Command start() {
        return complete(flywheel::start);
    }

    public Command stop() {
        return complete(flywheel::stop);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, flywheel);
    }
}
