package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Position.ENTER;
import static org.firstinspires.ftc.teamcode.game.Position.EXIT;
import static org.firstinspires.ftc.teamcode.game.Position.TARGET;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class AutoCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        config.alliance = RED;
        config.side = NORTH;
        auto = spy(new AutoCommands());
    }

    @Test
    public void testExecute() {
        auto.execute().initialize();
        verify(auto).delayStart();
    }

    @Test
    public void testDelayStart() {
        auto.delayStart().initialize();
        verify(wait).seconds(config.delay);
    }
}
