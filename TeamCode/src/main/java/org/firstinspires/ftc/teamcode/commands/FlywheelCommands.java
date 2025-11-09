package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class FlywheelCommands {
    public Command forward() {
        return complete(flywheel::forward);
    }

    public Command reverse() {
        return complete(flywheel::reverse);
    }

    public Command hold() {
        return complete(flywheel::hold);
    }

    public Command stop() {
        return complete(flywheel::stop);
    }

    public Command isReady() {
        return wait.until(flywheel::isReady);
    }

    public Command wheelUpToSpeed() {
        return Commands.wait.until(flywheel::isReady)
            .andThen(drive.rumble());
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, flywheel);
    }
}
