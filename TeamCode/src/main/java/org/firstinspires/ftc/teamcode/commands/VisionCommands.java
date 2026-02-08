package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.PURPLE;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline;

public class VisionCommands {
    public Command goalLock(boolean enabled) {
        return complete(
            () -> vision.goalLock(enabled)
        );
    }

    /** @noinspection unused*/
    public Command switchPipeline(Pipeline pipeline) {
        return complete(
            () -> vision.switchPipeline(pipeline, pipeline.index != 0)
        );
    }

    public Command scan() {
        return switchPipeline(PURPLE).andThen(
            complete(vision::scan)
        );
    }

    public Command chaseLock(boolean enabled) {
        return complete(
            () -> vision.goalLock(enabled)
        );
    }

    public Command nextElement() {
        return complete(vision::nextElement);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
