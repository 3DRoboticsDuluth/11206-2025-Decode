package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.function.BooleanSupplier;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class WaitCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        wait = spy(new WaitCommands());
        gamepad1 = new GamepadEx(new Gamepad());
    }

    @Test
    public void testForInterruptAConditionCoversConfigAndGamepadBranches() {
        wait.forInterruptA().initialize();

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait).until(captor.capture());

        config.interrupt = false;
        config.started = true;
        assert captor.getValue().getAsBoolean();

        config.interrupt = false;
        config.started = false;
        gamepad1.gamepad.start = false;
        gamepad1.gamepad.a = false;
        assert !captor.getValue().getAsBoolean();

        config.interrupt = true;
        config.started = false;
        gamepad1.gamepad.start = true;
        gamepad1.gamepad.a = false;
        assert !captor.getValue().getAsBoolean();

        gamepad1.gamepad.start = false;
        gamepad1.gamepad.a = true;
        assert captor.getValue().getAsBoolean();
    }

    @Test
    public void testForInterruptBConditionCoversConfigAndGamepadBranches() {
        wait.forInterruptB().initialize();

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait).until(captor.capture());

        config.interrupt = false;
        config.started = true;
        assert captor.getValue().getAsBoolean();

        config.interrupt = false;
        config.started = false;
        gamepad1.gamepad.start = false;
        gamepad1.gamepad.b = false;
        assert !captor.getValue().getAsBoolean();

        config.interrupt = true;
        config.started = false;
        gamepad1.gamepad.start = true;
        gamepad1.gamepad.b = false;
        assert !captor.getValue().getAsBoolean();

        gamepad1.gamepad.start = false;
        gamepad1.gamepad.b = true;
        assert captor.getValue().getAsBoolean();
    }

    @Test
    public void testSecondsAndMilliseconds() {
        wait.seconds(1.5);
        wait.milliseconds(10);
    }

    @Test
    public void testDohertyAndNoop() {
        wait.doherty();
        wait.doherty(2);
        wait.noop();
        verify(wait).seconds(0.4);
        verify(wait).seconds(0.8);
        verify(wait).seconds(0);
    }

    @Test
    public void testDebug() {
        wait.debug().initialize();

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait).until(captor.capture());

        gamepad1.gamepad.back = false;
        assert !captor.getValue().getAsBoolean();

        gamepad1.gamepad.back = true;
        assert captor.getValue().getAsBoolean();
    }
}
