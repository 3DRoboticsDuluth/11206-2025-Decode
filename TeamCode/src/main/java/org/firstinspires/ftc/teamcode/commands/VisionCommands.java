package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class VisionCommands {
    public Command goalLock(boolean enabled) {
        return complete(
            () -> vision.goalLock(enabled)
        );
    }

    public Command chaseLock(boolean enabled) {
        return complete(
            () -> vision.chaseLock(enabled)
        );
    }

    public Command nextElement() {
        return complete(vision::nextElement);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
