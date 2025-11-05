package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class IntakeControlsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new IntakeControls();
    }

    @Test
    public void testAAndDpadUpIntakes() {
        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_up = true);
        verify(intake.forward()).schedule(true);
    }

    @Test
    public void testAAndDpadDownReversesIntake() {
        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_down = true);
        verify(intake.reverse()).schedule(true);
    }

    @Test
    public void testAAndDpadLeftOrDpadRightStopsIntake() {
        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_left = true);
        verify(intake.stop()).schedule(true);
        input(() -> gamepad2.gamepad.a = true);
        input(() -> gamepad2.gamepad.dpad_right = true);
        verify(intake.stop()).schedule(true);
    }
}
