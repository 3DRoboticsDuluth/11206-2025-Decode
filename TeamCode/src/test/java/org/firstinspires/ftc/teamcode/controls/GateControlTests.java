package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class GateControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new GateControl();
    }

    @Test
    public void testBackAndDpadUpOpensGate() {
        input(() -> gamepad2.gamepad.back = true);
        verify(gate).open();
    }

    @Test
    public void testBackAndDpadDownClosesGate() {
        input(() -> gamepad2.gamepad.back = true);
        verify(gate).close();
    }
}
