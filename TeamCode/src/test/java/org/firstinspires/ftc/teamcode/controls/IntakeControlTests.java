package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class IntakeControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new IntakeControl();
    }

    @Test
    public void testAAndDpadUpIntakes() {
        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_up = true);
        verify(intake).forward();
    }

    @Test
    public void testAAndDpadDownReversesIntake() {
        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_down = true);
        verify(intake).reverse();
    }

    @Test
    public void testAAndDpadLeftOrDpadRightStopsIntake() {
        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_left = true);
        verify(intake).stop();

        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_right = true);
        verify(intake).stop();
    }
}
