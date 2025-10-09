package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Pipeline.RED_SAMPLE;
import static org.firstinspires.ftc.teamcode.game.Pipeline.RED_SPECIMEN;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class VisionCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        vision = spy(new VisionCommands());
    }
}
