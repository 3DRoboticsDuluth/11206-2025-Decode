package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline;

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

    public Command setPipeline(Pipeline pipeline) {
        return complete(
            () -> vision.switchPipeline(pipeline, true)
        );
    }

    public Command resetElement() {
        return new DeferredCommand(
            () -> complete(vision::resetElement), null
        );
    }

    public Command waitForElement(){
        return wait.until(
                () -> vision.element != null
        );
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
