package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.config;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change.NEXT;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change.PREV;
import static org.mockito.Mockito.verify;

import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

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
        verify(auto).intakeArtifact();
    }
    
    @Test
    public void testAAndDpadDownSpitArtifact() {
        input(() -> gamepad1.gamepad.a = true);
        input(() -> gamepad1.gamepad.dpad_down = true);
        verify(auto).spitArtifact();
    }
    
    @Test
    public void testAAndDpadLeftAndRightDoesAutoArtifact() {
        input(() -> gamepad1.gamepad.dpad_left = true);
        input(() -> gamepad1.gamepad.a = true);
        verify(auto).autoArtifact();

        input(() -> gamepad1.gamepad.dpad_right = true);
        input(() -> gamepad1.gamepad.a = true);
        verify(auto).autoArtifact();
    }
    
    @Test
    public void testBAndDpadUpDepositsNear() {
        input(() -> gamepad1.gamepad.dpad_up = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto).depositNear();
    }
    
    @Test
    public void testBAndDpadDownDepositsFar() {
        input(() -> gamepad1.gamepad.dpad_down = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto).depositFar();
    }

    @Test
    public void testBAndDpadLeftOrDpadRightDeposits() {
        input(() -> gamepad1.gamepad.dpad_right = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto).depositFromPose();

        input(() -> gamepad1.gamepad.dpad_left = true);
        input(() -> gamepad1.gamepad.b = true);
        verify(auto).depositFromPose();
    }

    @Test
    public void testYHumanPlayerZone() {
        input(() -> gamepad1.gamepad.y = true);
        verify(auto).humanPlayerZone();
    }

    @Test
    public void testXAndDpadUpOpensGate() {
        input(() -> gamepad1.gamepad.dpad_up = true);
        input(() -> gamepad1.gamepad.x = true);
        verify(gate).gateOpen();
    }

    @Test
    public void testXAndDpadDownClosesGate() {
        input(() -> gamepad1.gamepad.dpad_down = true);
        input(() -> gamepad1.gamepad.x = true);
        verify(gate).gateClose();
    }
}
