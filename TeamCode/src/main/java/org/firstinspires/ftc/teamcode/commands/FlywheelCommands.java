package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public class FlywheelCommands {
    public Command start() {
        return complete(flywheel::start);
    }

    public Command stop() {
        return complete(flywheel::stop);
    }

    public Command reverse() {
        return complete(flywheel::reverse);
    }
    public Command forFlyWheelReady() {
        return wait.until(flywheel::isAtTargetVelocity);
    }
    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, flywheel);
    }
}
