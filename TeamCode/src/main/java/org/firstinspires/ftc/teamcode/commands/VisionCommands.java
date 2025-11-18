package org.firstinspires.ftc.teamcode.commands;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public class VisionCommands {
    public Command switchPipeline(Pipeline pipeline) {
        return new InstantCommand(
            () -> Subsystems.vision.switchPipeline(pipeline.index, pipeline.index != 0),
            Subsystems.vision
        );
    }

    public Command startQRScan() {
        return new InstantCommand(
                () -> Subsystems.vision.startQrScan(),
                Subsystems.vision
        );
    }

    public Command stopQRScan() {
        return new InstantCommand(
                () -> Subsystems.vision.stopQrScan(),
                Subsystems.vision
        );
    }
}
