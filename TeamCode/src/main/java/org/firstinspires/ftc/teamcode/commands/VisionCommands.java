package org.firstinspires.ftc.teamcode.commands;

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

    public Command switchPipeline(Pipeline pipeline) {
        return complete(
            () -> vision.switchPipeline(pipeline, pipeline.index != 0)
        );
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, vision);
    }
}
