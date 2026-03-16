package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.BLUE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.ORANGE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.RED;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.TRANSPARENT;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.WHITE;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Supplier;

public class LightsSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        Subsystems.lights = new LightsSubsystem() {{
            errors = new ArrayList<>();
            prism = org.mockito.Mockito.mock(GoBildaPrismDriver.class);
        }};
    }

    @Test
    public void testPeriodicReturnsWhenUnready() {
        Subsystems.lights = new LightsSubsystem() {{
            errors.add("disabled");
            prism = org.mockito.Mockito.mock(GoBildaPrismDriver.class);
        }};

        Subsystems.lights.periodic();

        verify(telemetry, never()).addData(eq("Lights"), any());
    }

    @Test
    public void testPeriodicBeforeStartUsesAllianceColor() {
        config.started = false;
        config.alliance = Alliance.RED;
        Subsystems.lights.color = BLUE;

        Subsystems.lights.periodic();

        verify(Subsystems.lights.prism).insertAndUpdateAnimation(eq(GoBildaPrismDriver.LayerHeight.LAYER_0), any());
        ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
        verify(telemetry).addData(eq("Lights"), captor.capture());
        when(Subsystems.lights.prism.getNumberOfLEDs()).thenReturn(48);
        when(Subsystems.lights.prism.getCurrentFPS()).thenReturn(60);
        assert captor.getValue().get().toString().contains("48 leds");
    }

    @Test
    public void testPeriodicBeforeStartUsesBlueAlliance() {
        config.started = false;
        config.alliance = Alliance.BLUE;
        Subsystems.lights.color = RED;

        Subsystems.lights.periodic();

        verify(Subsystems.lights.prism).insertAndUpdateAnimation(eq(GoBildaPrismDriver.LayerHeight.LAYER_0), any());
    }

    @Test
    public void testPeriodicTeleopEndgameThresholds() {
        config.started = true;
        config.auto = false;

        Subsystems.lights.color = TRANSPARENT;
        when(TimingSubsystem.playTimer.seconds()).thenReturn(111.0);
        Subsystems.lights.periodic();

        clearInvocations(Subsystems.lights.prism);
        Subsystems.lights.color = TRANSPARENT;
        when(TimingSubsystem.playTimer.seconds()).thenReturn(101.0);
        Subsystems.lights.periodic();

        clearInvocations(Subsystems.lights.prism);
        Subsystems.lights.color = TRANSPARENT;
        when(TimingSubsystem.playTimer.seconds()).thenReturn(81.0);
        Subsystems.lights.periodic();

        clearInvocations(Subsystems.lights.prism);
        Subsystems.lights.color = TRANSPARENT;
        when(TimingSubsystem.playTimer.seconds()).thenReturn(79.0);
        Subsystems.lights.periodic();

        verify(Subsystems.lights.prism, never()).insertAndUpdateAnimation(eq(GoBildaPrismDriver.LayerHeight.LAYER_0), any());
    }

    @Test
    public void testPeriodicSkipsColorChangeForUnknownAllianceAndAutoStarted() {
        config.started = false;
        config.alliance = Alliance.UNKNOWN;
        Subsystems.lights.periodic();
        verify(Subsystems.lights.prism, never()).insertAndUpdateAnimation(eq(GoBildaPrismDriver.LayerHeight.LAYER_0), any());

        clearInvocations(Subsystems.lights.prism);
        config.started = true;
        config.auto = true;
        when(TimingSubsystem.playTimer.seconds()).thenReturn(120.0);
        Subsystems.lights.periodic();
        verify(Subsystems.lights.prism, never()).insertAndUpdateAnimation(eq(GoBildaPrismDriver.LayerHeight.LAYER_0), any());
    }

    @Test
    public void testSetSkipsSameColorAndUpdatesDifferentColor() {
        Subsystems.lights.color = RED;
        Subsystems.lights.set(RED);
        verify(Subsystems.lights.prism, never()).insertAndUpdateAnimation(eq(GoBildaPrismDriver.LayerHeight.LAYER_0), any());

        Subsystems.lights.set(ORANGE);
        verify(Subsystems.lights.prism).insertAndUpdateAnimation(eq(GoBildaPrismDriver.LayerHeight.LAYER_0), any());
    }
}
