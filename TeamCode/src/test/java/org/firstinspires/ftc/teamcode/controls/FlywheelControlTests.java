package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class FlywheelControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new FlywheelControl();
    }

    @Test
    public void testLeftAndRightTriggerMakeFlywheelStartAndStop() {
        input(() -> gamepad2.gamepad.left_trigger = 1);
        verify(flywheel.start()).schedule(true);
        input(() -> gamepad2.gamepad.left_trigger = 0);
        verify(flywheel.stop()).schedule(true);
        input(() -> gamepad2.gamepad.right_trigger = 1);
        verify(flywheel.start(), times(2)).schedule(true);
        input(() -> gamepad2.gamepad.right_trigger = 0);
        verify(flywheel.stop(), times(2)).schedule(true);
    }
    
    @Test
    public void testBAndDpadUpStartsFlywheel() {
        input(() -> gamepad2.gamepad.b = true);
        input(() -> gamepad2.gamepad.dpad_up = true);
        verify(flywheel.start()).schedule(true);
    }
    
    @Test
    public void testBAndDpadDownReversesFlywheel() {
        input(() -> gamepad2.gamepad.b = true);
        input(() -> gamepad2.gamepad.dpad_down = true);
        verify(flywheel.reverse()).schedule(true);
    }

    @Test
    public void testBAndDpadLeftOrRightStopsFlywheel() {
        input(() -> gamepad2.gamepad.b = true);
        input(() -> gamepad2.gamepad.dpad_left = true);
        verify(flywheel.stop()).schedule(true);
        input(() -> gamepad2.gamepad.dpad_left = false);
        input(() -> gamepad2.gamepad.dpad_right = true);
        verify(flywheel.stop(), times(2)).schedule(true);
    }
}
