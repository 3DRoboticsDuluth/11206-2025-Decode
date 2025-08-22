package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Pipeline.APRILTAG;
import static org.firstinspires.ftc.teamcode.game.Pipeline.YELLOW;
import static org.firstinspires.ftc.teamcode.game.Sample.NEUTRAL;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.game.Pipeline;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public class VisionCommands {
    public Command switchPipeline(Pipeline pipeline) {
        return new InstantCommand(
            () -> Subsystems.vision.switchPipeline(pipeline.index, pipeline.index != 0),
            Subsystems.vision
        );
    }
}
