package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.lights;
import static org.firstinspires.ftc.teamcode.commands.Commands.quanomous;
import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.BooleanSupplier;

import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.subsystems.LightsSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class AutoCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        config.alliance = RED;
        config.side = NORTH;
        auto = spy(new AutoCommands());
        quanomous = org.mockito.Mockito.mock(QuanomousCommands.class);
        org.mockito.Mockito.when(quanomous.execute()).thenReturn(new InstantCommand());
        lights = org.mockito.Mockito.mock(LightsCommands.class);
        org.firstinspires.ftc.teamcode.subsystems.Subsystems.lights = org.mockito.Mockito.mock(LightsSubsystem.class);
    }

    @Test
    public void testDelayStart() {
        auto.delayStart().initialize();
        verify(wait).seconds(config.delay);
    }

    @Test
    public void testExecute() {
        doReturn(new InstantCommand(), new InstantCommand()).when(auto).stop();
        auto.execute();
        verify(auto).delayStart();
        verify(quanomous).execute();
        verify(auto, times(2)).stop();
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
    public void testDepositSouthFarUsesShortDistanceAndDelay() {
        config.pose.x = -3 * TILE_WIDTH;
        doReturn(wait.noop()).when(auto).intakeStop();
        doReturn(wait.noop()).when(auto).depositStart();

        auto.deposit(SOUTH, 0, 0).initialize();

        verify(drive).untilDistance(-24);
        verify(wait).doherty(1);
    }

    @Test
    public void testReleaseGate() {
        auto.releaseGate().initialize();
        verify(drive).toGate();
        verify(gate).close();
    }

    @Test
    public void testIntakeSequence() {
        auto.intake(1).initialize();
        verify(drive).toSpike0();
        verify(drive).toSpike1();
        verify(drive).toSpike2();
        verify(drive).toSpike3();
        verify(auto).depositStop();
        verify(auto).intakeStart();
        verify(drive).untilDistance(TILE_WIDTH * -2);
        verify(drive).setPowerIntake();
    }

    @Test
    public void testGateIntake() {
        auto.gateIntake();
        verify(auto).intakeStart();
        verify(drive).toGate();
        verify(drive).setPowerHigh();
        verify(drive).toGateIntake();
        verify(wait).seconds(1.5);
        verify(drive).setPowerLow();
        verify(drive).toGateIntakeDepart();
        verify(drive).setPowerAuto();
    }

    @Test
    public void testDrivePose() {
        Pose pose = new Pose(1, 2, 3);
        auto.drive(pose);
        verify(drive).curve(pose);
        verify(auto).depositStop();
    }

    @Test
    public void testChase() {
        when(vision.chaseLock(true)).thenReturn(new InstantCommand());
        when(lights.set(org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.TRANSPARENT)).thenReturn(new InstantCommand());
        when(drive.toChase(anyInt())).thenReturn(new InstantCommand());
        when(drive.untilDistance(TILE_WIDTH * -1)).thenReturn(new InstantCommand());
        when(drive.setPowerLow()).thenReturn(new InstantCommand());
        when(wait.milliseconds(anyLong())).thenReturn(new InstantCommand());
        when(vision.resetElement()).thenReturn(new InstantCommand());
        when(wait.doherty()).thenReturn(new InstantCommand());
        when(drive.setPowerAuto()).thenReturn(new InstantCommand());
        doReturn(wait.noop()).when(auto).intakeStart();
        doReturn(wait.noop()).when(auto).deposit(any(), org.mockito.ArgumentMatchers.anyDouble(), org.mockito.ArgumentMatchers.anyDouble());

        auto.chase(2).initialize();

        verify(vision).chaseLock(true);
        verify(drive).toChaseScan();
        verify(vision).resetElement();
        verify(auto).intakeStart();
        verify(auto).deposit(NORTH, 0, 0);
    }

    @Test
    public void testParkConditionHandlesGoalLockStates() {
        auto.park(false, NavSubsystem.Axial.CENTER, NavSubsystem.Lateral.CENTER);

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait).until(captor.capture());

        config.goalLock = true;
        assert !captor.getValue().getAsBoolean();

        config.goalLock = false;
        assert captor.getValue().getAsBoolean();

        verify(drive).toParking(anyBoolean(), any(), any());
    }

    @Test
    public void testStopCallsDriveStop() {
        auto.stop().initialize();
        verify(drive).goalLock(false);
        verify(drive).chaseLock(false);
        verify(drive).stop();
        verify(intake).stop();
        verify(conveyor).stop();
        verify(gate).close();
        verify(flywheel).stop();
    }
}
