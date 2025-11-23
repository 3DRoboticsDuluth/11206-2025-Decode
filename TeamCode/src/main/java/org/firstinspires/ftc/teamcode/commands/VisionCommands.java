package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline;

public class VisionCommands {
    public Command switchPipeline(Pipeline pipeline) {
        return complete(
            () -> vision.switchPipeline(pipeline.index, pipeline.index != 0)
        );
    }

    public Command startQRScan() {
        return complete(
            () -> vision.startQrScan()
        );
    }

    public Command stopQRScan() {
        return complete(
            () -> vision.stopQrScan()
        );
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, vision);
    }
}
