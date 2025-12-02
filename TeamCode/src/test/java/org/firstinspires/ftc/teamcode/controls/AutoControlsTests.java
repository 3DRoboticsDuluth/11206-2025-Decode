package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class AutoControlsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new AutoControls();
    }
    
    @Test
    public void testAAndDpadUpIntakesArtifact() {
        input(() -> gamepad1.gamepad.a = true);
        input(() -> gamepad1.gamepad.dpad_up = true);
        verify(auto.intakeStart()).schedule(true);
    }

    @Test
    public void testBAndDpadUpDepositsNear() {
        input(() -> gamepad1.gamepad.dpad_up = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto.depositSouth(0, 0)).schedule(true);
    }
    
    @Test
    public void testBAndDpadDownDepositsFar() {
        input(() -> gamepad1.gamepad.dpad_down = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto.depositNorth(0, 0)).schedule(true);
    }
}
