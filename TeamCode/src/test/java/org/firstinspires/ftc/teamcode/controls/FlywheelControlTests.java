package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
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
        input(() -> gamepad2.gamepad.right_trigger = 1);
        verify(flywheel).start();

        input(() -> gamepad2.gamepad.left_trigger = 1);
        verify(flywheel).start();
    }
    
    @Test
    public void testBAndDpadUpStartsFlywheel() {
        input(() -> gamepad2.gamepad.b = true);
        input(() -> gamepad2.gamepad.dpad_up = true);
        verify(flywheel).start();
    }
    
    @Test
    public void testBAndDpadDownReversesFlywheel() {
        input(() -> gamepad2.gamepad.b = true);
        input(() -> gamepad2.gamepad.dpad_down = true);
        verify(flywheel).reverse();
    }

    @Test
    public void testBAndDpadLeftOrRightStopsFlywheel() {
        input(() -> gamepad2.gamepad.b = true);
        input(() -> gamepad2.gamepad.dpad_left = true);
        verify(flywheel).stop();

        input(() -> gamepad2.gamepad.b = true);
        input(() -> gamepad2.gamepad.dpad_right = true);
        verify(flywheel).stop();
    }
}
