package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.sorting;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class SortingControlsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new SortingControls();
    }

    @Test
    public void testLeftTrigger() {
        input(() -> gamepad2.gamepad.left_trigger = 1);
        verify(sorting.sort()).schedule(true);
        input(() -> gamepad2.gamepad.left_trigger = 0);
        verify(sorting.pass()).schedule(true);
    }

    @Test
    public void testRightTrigger() {
        input(() -> gamepad2.gamepad.right_trigger = 1);
        verify(sorting.sort()).schedule(true);
        input(() -> gamepad2.gamepad.right_trigger = 0);
        verify(sorting.pass()).schedule(true);
    }
}
