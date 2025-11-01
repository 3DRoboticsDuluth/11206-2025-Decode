package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class ConveyorControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new ConveyorControl();
    }
    
    @Test
    public void testXAndDpadUpMoveConveyorForward() {
        input(() -> gamepad2.gamepad.x = true);
        input(() -> gamepad2.gamepad.dpad_up = true);
        verify(conveyor).forward();
    }
    
    @Test
    public void testAAndDpadDownSpitArtifact() {
        input(() -> gamepad2.gamepad.x = true);
        input(() -> gamepad2.gamepad.dpad_down = true);
        verify(conveyor).reverse();
    }
    
    @Test
    public void testXAndDpadLeftOrDpadRightStopConveyor() {
        input(() -> gamepad2.gamepad.dpad_left = true);
        input(() -> gamepad2.gamepad.x = true);
        verify(conveyor).stop();

        input(() -> gamepad2.gamepad.dpad_right = true);
        input(() -> gamepad2.gamepad.x = true);
        verify(conveyor).stop();
    }
}
