package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class DriveControlsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new DriveControls();
    }
    
    @Test
    public void testInput() {
        input(() -> gamepad1.gamepad.left_stick_y = 0.33f);
        input(() -> gamepad1.gamepad.left_stick_x = 0.66f);
        input(() -> gamepad1.gamepad.right_stick_x = 1.00f);
        verify(drive).input(any(), any(), any());
    }
    
    @Test
    public void testDpadDownSetPowerLow() {
        input(() -> gamepad1.gamepad.dpad_down = true);
        verify(drive.setPowerLow()).schedule(true);
    }

    @Test
    public void testDpadLeftSetsPowerMedium() {
        input(() -> gamepad1.gamepad.dpad_left = true);
        verify(drive.setPowerMedium()).schedule(true);
    }

    @Test
    public void testDpadRightSetsPowerMedium() {
        input(() -> gamepad1.gamepad.dpad_right = true);
        verify(drive.setPowerMedium()).schedule(true);
    }

    @Test
    public void testDpadUpSetPowerHigh() {
        input(() -> gamepad1.gamepad.dpad_up = true);
        verify(drive.setPowerHigh()).schedule(true);
    }

    @Test
    public void testBackAndStartToggleRobotCentric() {
        config.robotCentric = false;
        input(() -> gamepad1.gamepad.back = true);
        input(() -> gamepad1.gamepad.start = true);
        assert config.robotCentric;
        input(() -> gamepad1.gamepad.back = false);
        input(() -> gamepad1.gamepad.start = false);
        assert config.robotCentric;
        input(() -> gamepad1.gamepad.back = true);
        input(() -> gamepad1.gamepad.start = true);
        assert !config.robotCentric;
    }
}
