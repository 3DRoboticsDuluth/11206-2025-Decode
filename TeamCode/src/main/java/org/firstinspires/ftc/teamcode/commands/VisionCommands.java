package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

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

    public Command startArtifactScan() {
        return complete(
            () -> vision.startArtifactScan()
        );
    }

    public Command stopArtifactScan() {
        return complete(
            () -> vision.stopArtifactScan()
        );
    }

    public Command waitForArtifact() {
        return wait.until(() -> vision.hasArtifactDetected());
    }

    public Command scanAndDriveToArtifact() {
        return new SequentialCommandGroup(
            startArtifactScan(),
            waitForArtifact(),
            stopArtifactScan(),
            new InstantCommand(() -> {
                if (vision.elementPose != null) {
                    Commands.drive.to(vision.elementPose).schedule();
                }
            })
        );
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, vision);
    }
}