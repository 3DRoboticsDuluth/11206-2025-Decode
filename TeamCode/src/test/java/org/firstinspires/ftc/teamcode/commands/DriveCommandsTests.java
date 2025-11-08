package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
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
        verify(drive).setPowerLow();
    }

    @Test
    public void testSetPowerMedium() {
        drive.setPowerMedium().initialize();
        verify(drive).setPowerMedium();
    }

    @Test
    public void testSetPowerHigh() {
        drive.setPowerHigh().initialize();
        verify(drive).setPowerHigh();
    }

    @Test
    public void testToStart() {
        drive.toStart().initialize();
        verify(Subsystems.nav, times(1)).getStartPose();
    }

    @Test
    public void testToClosestArtifact() {
        drive.toClosestArtifact().initialize();
        // TODO: Add verification
    }

    @Test
    public void testToLaunchNear() {
        drive.toLaunchNear().initialize();
        verify(Subsystems.nav, times(1)).getLaunchNearPose();
    }

    @Test
    public void testToLaunchFar() {
        drive.toLaunchFar().initialize();
        verify(Subsystems.nav, times(1)).getLaunchFarPose();
    }

    @Test
    public void testToLaunchAlign() {
        drive.toLaunchAlign().initialize();
        verify(Subsystems.nav, times(1)).getLaunchAlignPose();
    }

    @Test
    public void testToLoadingZone() {
        drive.toLoadingZone().initialize();
        verify(Subsystems.nav, times(1)).getSpike0();
    }
}
