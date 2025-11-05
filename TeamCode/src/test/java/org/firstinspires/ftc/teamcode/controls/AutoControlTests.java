package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class AutoControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new AutoControl();
    }
    
    @Test
    public void testAAndDpadUpIntakesArtifact() {
        input(() -> gamepad1.gamepad.a = true);
        input(() -> gamepad1.gamepad.dpad_up = true);
        verify(auto.intakeArtifact()).schedule(true);
    }
    
    @Test
    public void testAAndDpadDownSpitArtifact() {
        input(() -> gamepad1.gamepad.a = true);
        input(() -> gamepad1.gamepad.dpad_down = true);
        verify(auto.spitArtifact()).schedule(true);
    }
    
    @Test
    public void testAAndDpadLeftAndRightDoesAutoArtifact() {
        input(() -> gamepad1.gamepad.dpad_left = true);
        input(() -> gamepad1.gamepad.a = true);
        verify(auto.autoArtifact()).schedule(true);
        input(() -> gamepad1.gamepad.dpad_right = true);
        input(() -> gamepad1.gamepad.a = true);
        verify(auto.autoArtifact()).schedule(true);
    }
    
    @Test
    public void testBAndDpadUpDepositsNear() {
        input(() -> gamepad1.gamepad.dpad_up = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto.depositNear()).schedule(true);
    }
    
    @Test
    public void testBAndDpadDownDepositsFar() {
        input(() -> gamepad1.gamepad.dpad_down = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto.depositFar()).schedule(true);
    }

    @Test
    public void testBAndDpadLeftOrDpadRightDeposits() {
        input(() -> gamepad1.gamepad.dpad_right = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto.depositFromPose()).schedule(true);
        input(() -> gamepad1.gamepad.dpad_left = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto.depositFromPose()).schedule(true);
    }

    @Test
    public void testYHumanPlayerZone() {
        input(() -> gamepad1.gamepad.y = true);
        verify(auto.humanPlayerZone()).schedule(true);
    }
}
