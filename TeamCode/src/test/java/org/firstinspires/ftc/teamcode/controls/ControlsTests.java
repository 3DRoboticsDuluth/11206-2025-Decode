package org.firstinspires.ftc.teamcode.controls;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class ControlsTests extends TestHarness {
    @Test
    public void testInitializeAuto() {
        Controls.initializeAuto();
    }

    @Test
    public void testInitializeTeleop() {
        Controls.initializeTeleop();
    }
}
