package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.playTimer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Supplier;

public class TimingSubsystemTests extends TestHarness {
    @Test
    public void testPeriodicBeforeAndAfterEndgameRumble() {
        TimingSubsystem subsystem = new TimingSubsystem();
        TimingSubsystem.periodicTimer = mock(com.qualcomm.robotcore.util.ElapsedTime.class);

        when(playTimer.seconds()).thenReturn(10.0, 10.0, 80.0, 80.0);
        when(TimingSubsystem.periodicTimer.milliseconds()).thenReturn(20.0);
        when(TimingSubsystem.periodicTimer.seconds()).thenReturn(0.02, 0.02);

        subsystem.periodic();

        verify(gamepad1.gamepad, never()).rumble(1.0, 1.0, 1000);
        verify(gamepad2.gamepad, never()).rumble(1.0, 1.0, 1000);

        ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
        verify(telemetry).addData(eq("Timing"), captor.capture());
        assert captor.getValue().get().toString().contains("10.0s");
        verify(TimingSubsystem.periodicTimer).reset();
        verify(telemetry).update();

        when(playTimer.seconds()).thenReturn(76.0, 76.0, 76.0, 76.0);
        subsystem.periodic();
        subsystem.periodic();

        verify(gamepad1.gamepad).rumble(1.0, 1.0, 1000);
        verify(gamepad2.gamepad).rumble(1.0, 1.0, 1000);
    }
}
