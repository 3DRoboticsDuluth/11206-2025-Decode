package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.RESPONSIVENESS_INCREMENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class DriveControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new DriveControl();
    }
    
    @Test
    public void testInput() {
        input(() -> gamepad1.gamepad.left_stick_y = 0.33f);
        input(() -> gamepad1.gamepad.left_stick_x = 0.66f);
        input(() -> gamepad1.gamepad.right_stick_x = 1.00f);
        verify(drive).input(any(), any(), any());
    }
    
    @Test
    public void testBackAndDpadDownSetPowerLow() {
        input(() -> gamepad1.gamepad.back = true);
        input(() -> gamepad1.gamepad.dpad_down = true);
        verify(drive.setPowerLow()).schedule(true);
    }

    @Test
    public void testBackAndDpadLeftOrDpadRightSetPowerMedium() {
        input(() -> gamepad1.gamepad.back = true);
        input(() -> gamepad1.gamepad.dpad_left = true);
        verify(drive.setPowerMedium()).schedule(true);
    }

    @Test
    public void testBackAndDpadRightOrDpadRightSetPowerMedium() {
        input(() -> gamepad1.gamepad.back = true);
        input(() -> gamepad1.gamepad.dpad_right = true);
        verify(drive.setPowerMedium()).schedule(true);
    }

    @Test
    public void testBackAndDpadUpSetPowerHigh() {
        input(() -> gamepad1.gamepad.back = true);
        input(() -> gamepad1.gamepad.dpad_up = true);
        verify(drive.setPowerHigh()).schedule(true);
    }

    @Test
    public void testLeftBumperResponsivenessIncrease() {
        double responsiveness = 0.5;
        config.responsiveness = responsiveness;
        input(() -> gamepad1.gamepad.left_bumper = true);
        assert config.responsiveness == responsiveness - RESPONSIVENESS_INCREMENT;
    }

    @Test
    public void testRightBumperResponsivenessIncrease() {
        double responsiveness = 0.5;
        config.responsiveness = responsiveness;
        input(() -> gamepad1.gamepad.right_bumper = true);
        assert config.responsiveness == responsiveness + RESPONSIVENESS_INCREMENT;
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
