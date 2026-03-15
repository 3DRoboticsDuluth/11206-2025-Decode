package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
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
    public void testLeftTriggerStartsIntake() {
        input(() -> gamepad1.gamepad.left_trigger = 0.3f);
        verify(auto.intakeStart()).schedule(true);
    }

    @Test
    public void testLeftTriggerReleaseStopsIntake() {
        input(() -> gamepad1.gamepad.left_trigger = 0.3f);
        input(() -> gamepad1.gamepad.left_trigger = 0.0f);
        verify(auto.intakeStop()).schedule(true);
    }
    
    @Test
    public void testRightTriggerStartsDeposit() {
        input(() -> gamepad1.gamepad.right_trigger = 0.3f);
        verify(auto.depositStart()).schedule(true);
    }

    @Test
    public void testRightTriggerReleaseStopsDeposit() {
        input(() -> gamepad1.gamepad.right_trigger = 0.3f);
        input(() -> gamepad1.gamepad.right_trigger = 0.0f);
        verify(auto.depositStop()).schedule(true);
    }

    @Test
    public void testYStopsAuto() {
        input(() -> gamepad1.gamepad.y = true);
        verify(auto.stop()).schedule(true);
    }

    @Test
    public void testGamepad2ATogglesGoalLock() {
        input(() -> gamepad2.gamepad.a = true);
        verify(auto.goalLock(true)).schedule(true);
    }
}
