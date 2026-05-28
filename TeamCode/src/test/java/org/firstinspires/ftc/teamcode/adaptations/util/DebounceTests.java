package org.firstinspires.ftc.teamcode.adaptations.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.junit.Test;

public class DebounceTests {
    private static final double THRESHOLD = 0.125;

    @Test
    public void testTriggeredReturnsTrueAfterThreshold() {
        ElapsedTime timer = mock(ElapsedTime.class);
        Debounce debounce = new Debounce(timer);

        when(timer.seconds()).thenReturn(THRESHOLD);

        assert debounce.triggered(true, THRESHOLD);
    }

    @Test
    public void testTriggeredReturnsFalseBeforeThreshold() {
        ElapsedTime timer = mock(ElapsedTime.class);
        Debounce debounce = new Debounce(timer);

        when(timer.seconds()).thenReturn(THRESHOLD / 2);

        assert !debounce.triggered(true, THRESHOLD);
    }

    @Test
    public void testHeldSignalDoesNotRiseAgain() {
        ElapsedTime timer = mock(ElapsedTime.class);
        Debounce debounce = new Debounce(timer);

        when(timer.seconds()).thenReturn(THRESHOLD);

        assert debounce.triggered(true, THRESHOLD);
        assert !debounce.triggered(true, THRESHOLD);
    }

    @Test
    public void testSignalResetsTimerWhileTripped() {
        ElapsedTime timer = mock(ElapsedTime.class);
        Debounce debounce = new Debounce(timer);

        debounce.triggered(true, THRESHOLD);

        verify(timer).reset();
    }

    @Test
    public void testResetClearsPreviousState() {
        ElapsedTime timer = mock(ElapsedTime.class);
        Debounce debounce = new Debounce(timer);

        when(timer.seconds()).thenReturn(THRESHOLD);

        debounce.triggered(true, THRESHOLD);
        debounce.reset();

        assert debounce.triggered(true, THRESHOLD);
    }

    @Test
    public void testThresholdIsProvidedAtCallTime() {
        ElapsedTime timer = mock(ElapsedTime.class);
        Debounce debounce = new Debounce(timer);

        when(timer.seconds()).thenReturn(THRESHOLD / 2);

        assert debounce.triggered(true, THRESHOLD / 4);
    }
}
