package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_HIGH;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_LOW;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_MEDIUM;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class DriveCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        drive = spy(new DriveCommands());
        doReturn(wait.noop())
            .when(drive)
            .to(any(), anyBoolean());
    }

    @Test
    public void testSetPowerLow() {
        drive.setPowerLow().initialize();
        verify(Subsystems.drive.follower).setMaxPower(POWER_LOW);
    }

    @Test
    public void testSetPowerMedium() {
        drive.setPowerMedium().initialize();
        verify(Subsystems.drive.follower).setMaxPower(POWER_MEDIUM);
    }

    @Test
    public void testSetPowerHigh() {
        drive.setPowerHigh().initialize();
        verify(Subsystems.drive.follower).setMaxPower(POWER_HIGH);
    }

    @Test
    public void testToStart() {
        drive.toStart().initialize();
        verify(nav).getStartPose();
    }

    @Test
    public void testToClosestArtifact() {
        drive.toClosestArtifact().initialize();
        // TODO: Add verification
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
        verify(nav).getGatePose();
    }

    @Test
    public void testBase() {
        drive.toBase().initialize();
        verify(nav).getBasePose();
    }
}
