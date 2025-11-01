package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
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
    public void testPrepareDeposit() {
        auto.prepareDeposit().initialize();
        verify(gate).close();
        verify(flywheel).start();
        verify(flywheel).isReady();
    }

    @Test
    public void testDeposit() {
        auto.deposit().initialize();
        verify(intake).forward();
        verify(conveyor).forward();
        verify(gate).open();
        verify(deflector).compensate();
    }

    @Test
    public void testIntakeArtifact() {
        auto.intakeArtifact().initialize();
        verify(intake).forward();
        verify(conveyor).forward();
        verify(gate).close();
    }

    @Test
    public void testSpitArtifact() {
        auto.spitArtifact().initialize();
        verify(intake).reverse();
        verify(conveyor).reverse();
        verify(gate).close();
    }

    @Test
    public void testAutoArtifact() {
        auto.autoArtifact().initialize();
        verify(drive).toClosestArtifact();
        verify(auto).intakeArtifact();
    }

    @Test
    public void testDepositNear() {
        auto.depositNear().initialize();
        verify(drive).toLaunchNear();
        verify(auto).prepareDeposit();
        verify(auto).deposit();
    }

    @Test
    public void testDepositFar() {
        auto.depositFar().initialize();
        verify(drive).toLaunchFar();
        verify(auto).prepareDeposit();
        verify(auto).deposit();
    }

    @Test
    public void testDepositFromPose() {
        auto.depositFromPose().initialize();
        verify(drive).toLaunchAlign();
        verify(auto).prepareDeposit();
        verify(auto).deposit();
    }

    @Test
    public void testHumanPlayerZone() {
        auto.humanPlayerZone().initialize();
        verify(drive).toLoadingZone();
        verify(gate).close();
    }
}
