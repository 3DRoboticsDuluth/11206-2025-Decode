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
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
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
        verify(auto).goalLock(false);
        verify(intake).forward();
        verify(conveyor).forward();
        verify(gate).close();
    }

    @Test
    public void testIntakeStop() {
        auto.intakeStop().initialize();
        verify(auto).goalLock(true);
        verify(flywheel).forward();
        verify(conveyor).reverse();
        verify(gate).hold();
        verify(wait).doherty(2);
        verify(conveyor).stop();
        verify(intake).hold();
    }

    @Test
    public void testDepositStart() {
        auto.depositStart().initialize();
        verify(auto).goalLock(true);
        verify(gate).open();
        verify(intake).forward();
        verify(flywheel).forward();
        verify(conveyor).launch();
    }

    @Test
    public void testDepositStop() {
        auto.depositStop().initialize();
        verify(auto).goalLock(false);
        verify(conveyor).stop();
        verify(flywheel).stop();
        verify(intake).stop();
    }

    @Test
    public void testDepositSouth() {
        doReturn(wait.noop()).when(auto).intakeStop();
        doReturn(wait.noop()).when(auto).depositStart();
        auto.deposit(SOUTH, 0, 0).initialize();
        verify(drive).toDepositSouth(0, 0);
        verify(auto).intakeStop();
        verify(drive).setPowerAuto();
    }

    @Test
    public void testDepositNorth() {
        doReturn(wait.noop()).when(auto).intakeStop();
        doReturn(wait.noop()).when(auto).depositStart();
        auto.deposit(NORTH, 0, 0).initialize();
        verify(drive).toDepositNorth(0, 0);
        verify(auto).intakeStop();
        verify(drive).setPowerAuto();
    }

    @Test
    public void testReleaseGate() {
        auto.releaseGate().initialize();
        verify(drive).toGate();
        verify(gate).close();
    }
}
