package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class DeflectorControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new DeflectorControl();
    }
    
    @Test
    public void testXAndDpadUpMoveConveyorForward() {
        input(() -> gamepad2.gamepad.y = true);
        input(() -> gamepad2.gamepad.dpad_up = true);
        verify(deflector.up()).schedule(true);
    }
    
    @Test
    public void testAAndDpadDownSpitArtifact() {
        input(() -> gamepad2.gamepad.y = true);
        input(() -> gamepad2.gamepad.dpad_down = true);
        verify(deflector.down()).schedule(true);
    }
}
