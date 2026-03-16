package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.lights;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.RED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.LightsSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class LightsCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        Subsystems.lights = mock(LightsSubsystem.class);
        lights = new LightsCommands();
    }

    @Test
    public void testSet() {
        lights.set(RED).initialize();
        verify(Subsystems.lights).set(RED);
    }
}
