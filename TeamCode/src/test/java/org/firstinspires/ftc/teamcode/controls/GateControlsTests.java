package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class GateControlsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new GateControls();
    }

    @Test
    public void testBackAndDpadUpOpensGate() {
        input(() -> gamepad2.gamepad.back = true);
        input(() -> gamepad2.gamepad.dpad_up = true);
        verify(gate.open()).schedule(true);
    }

    @Test
    public void testBackAndDpadDownClosesGate() {
        input(() -> gamepad2.gamepad.back = true);
        input(() -> gamepad2.gamepad.dpad_down = true);
        verify(gate.close()).schedule(true);
    }
}
