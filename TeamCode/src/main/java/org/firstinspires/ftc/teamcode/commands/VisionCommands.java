package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem.ELEMENT_RADIUS;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
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
        return complete(vision::resetElement);
    }

    public Command waitForElement(){
        return wait.until(
            () -> vision.element != null
        );
    }

    public Command setDefaultElement() {
        return complete(vision::setBackupElement);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
