package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_HIGH;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_LOW;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_MEDIUM;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.follower;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.pedropathing.paths.PathBuilder;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.junit.Test;

public class DriveCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        nav = spy(new NavSubsystem());
        drive = spy(new DriveCommands());
        doReturn(wait.noop())
            .when(drive)
            .follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), anyBoolean());
        doReturn(wait.noop())
            .when(drive)
            .untilDistance(anyDouble());
    }

    @Test
    public void testSetPowerLow() {
        drive.setPowerLow().initialize();
        verify(follower).setMaxPower(POWER_LOW);
    }

    @Test
    public void testSetPowerMedium() {
        drive.setPowerMedium().initialize();
        verify(follower).setMaxPower(POWER_MEDIUM);
    }

    @Test
    public void testSetPowerHigh() {
        drive.setPowerHigh().initialize();
        verify(follower).setMaxPower(POWER_HIGH);
    }

    @Test
    public void testToStart() {
        drive.toStart().initialize();
        verify(nav).getStartPose();
    }

    @Test
    public void testToLaunchNear() {
        drive.toDepositSouth(0, 0).initialize();
        verify(nav).getDepositSouthPose(0, 0);
    }

    @Test
    public void testToLaunchFar() {
        drive.toDepositNorth(0, 0).initialize();
        verify(nav).getDepositNorthPose(0, 0);
    }

    @Test
    public void testToGate() {
        drive.toGate().initialize();
        verify(nav, times(2)).getGatePose();
    }

    @Test
    public void testBase() {
        drive.toBase().initialize();
        verify(nav).getBasePose();
    }
}
