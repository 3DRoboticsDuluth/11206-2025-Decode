package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class DriveCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        drive = spy(new DriveCommands());
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
}
