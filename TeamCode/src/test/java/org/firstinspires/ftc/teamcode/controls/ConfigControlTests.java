package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change.NEXT;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change.PREV;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class ConfigControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new ConfigControl();
    }
    
    @Test
    public void testBackButtonActivatesEditableMode() {
        input(() -> gamepad1.gamepad.back = true);
        verify(config).setEditable(true);
        input(() -> gamepad1.gamepad.back = false);
        verify(config).setEditable(false);
    }
    
    @Test
    public void testDpadUpChangesItemPrev() {
        input(() -> gamepad1.gamepad.dpad_up = true);
        verify(config).changeItem(PREV);
    }
    
    @Test
    public void testDpadDownChangesItemNext() {
        input(() -> gamepad1.gamepad.dpad_down = true);
        verify(config).changeItem(NEXT);
    }
    
    @Test
    public void testDpadLeftChangesValuePrev() {
        input(() -> gamepad1.gamepad.dpad_left = true);
        verify(config).changeValue(PREV);
    }
    
    @Test
    public void testDpadRightChangesValueNext() {
        input(() -> gamepad1.gamepad.dpad_right = true);
        verify(config).changeValue(NEXT);
    }
}
