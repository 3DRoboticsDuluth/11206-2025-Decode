package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.mockito.Mockito.spy;
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
    public void testDelayStart() {
        auto.delayStart().initialize();
        verify(wait).seconds(config.delay);
    }

    @Test
    public void testIntakeStart() {
        auto.intakeStart().initialize();
        verify(intake).forward();
        verify(conveyor).forward();
        verify(gate).close();
        verify(flywheel).hold();
    }

    @Test
    public void testIntakeStop() {
        auto.intakeStop().initialize();
        verify(conveyor).reverse();
        verify(wait).doherty(3);
        verify(conveyor).stop();
        verify(intake).hold();
        verify(gate).open();
        verify(wait).doherty(2);
        verify(flywheel).forward();
        verify(flywheel).isReady();
        verify(drive).rumble();
    }

    @Test
    public void testDepositStart() {
        auto.depositStart().initialize();
        verify(flywheel).forward();
        verify(flywheel).isReady();
        verify(conveyor).launch();
    }

    @Test
    public void testDepositStop() {
        auto.depositStop().initialize();
        verify(conveyor).stop();
        verify(flywheel).stop();
        verify(intake).stop();
    }

    @Test
    public void testDepositNear() {
        auto.depositNear().initialize();
        verify(drive).toDepositSouth();
        verify(auto).depositStart();
        verify(wait).doherty(3);
        verify(auto).depositStop();
    }

    @Test
    public void testDepositFar() {
        auto.depositFar().initialize();
        verify(auto).depositStart();
        verify(wait).doherty(3);
        verify(auto).depositStop();
    }

    @Test
    public void testReleaseGate() {
        auto.releaseGate().initialize();
        verify(drive).toGate();
        verify(drive).forward(3);
        verify(wait).seconds(1);
        verify(drive).forward(-3);
    }
}
