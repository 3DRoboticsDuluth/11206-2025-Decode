package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.sorting;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.seattlesolvers.solverslib.command.CommandScheduler;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class SortingControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new SortingControl();
    }

    @Test
    public void testLeftTrigger() {
        input(() -> gamepad2.gamepad.left_trigger = 1);
        verify(sorting).sort();
    }

    @Test
    public void testRightTrigger() {
        input(() -> gamepad2.gamepad.left_trigger = 1);
        verify(sorting).pass();
    }
}
