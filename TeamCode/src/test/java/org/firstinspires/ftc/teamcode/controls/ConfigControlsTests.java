package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change.NEXT;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change.PREV;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class ConfigControlsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new ConfigControls();
    }
    
    @Test
    public void testBackButtonActivatesEditableMode() {
        input(() -> gamepad1.gamepad.back = true);
        verify(config.setEditable(true)).schedule(true);
        input(() -> gamepad1.gamepad.back = false);
        verify(config.setEditable(false)).schedule(true);
    }
    
    @Test
    public void testDpadUpChangesItemPrev() {
        input(() -> gamepad1.gamepad.dpad_up = true);
        verify(config.changeItem(PREV)).schedule(true);
    }
    
    @Test
    public void testDpadDownChangesItemNext() {
        input(() -> gamepad1.gamepad.dpad_down = true);
        verify(config.changeItem(NEXT)).schedule(true);
    }
    
    @Test
    public void testDpadLeftChangesValuePrev() {
        input(() -> gamepad1.gamepad.dpad_left = true);
        verify(config.changeValue(PREV)).schedule(true);
    }
    
    @Test
    public void testDpadRightChangesValueNext() {
        input(() -> gamepad1.gamepad.dpad_right = true);
        verify(config.changeValue(NEXT)).schedule(true);
    }
}
