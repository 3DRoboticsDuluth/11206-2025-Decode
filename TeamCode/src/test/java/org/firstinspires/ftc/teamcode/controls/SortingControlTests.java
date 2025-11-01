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
    public void testRightTrigger() {
        when(gamepad2.getTrigger(RIGHT_TRIGGER)).thenReturn(0.0);
        CommandScheduler.getInstance().run();
        verify(sorting, never()).sort();
        verify(sorting, never()).pass();

        when(gamepad2.getTrigger(RIGHT_TRIGGER)).thenReturn(1.0);
        CommandScheduler.getInstance().run();
        verify(sorting).sort();

        when(gamepad2.getTrigger(RIGHT_TRIGGER)).thenReturn(0.0);
        CommandScheduler.getInstance().run();
        verify(sorting).pass();
    }
}
