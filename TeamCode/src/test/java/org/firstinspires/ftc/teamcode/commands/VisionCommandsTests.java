package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class VisionCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        vision = spy(new VisionCommands());
    }

    @Test
    public void testGoalLock() {
        vision.goalLock(true).initialize();
        verify(Subsystems.vision).goalLock(true);
    }

    @Test
    public void testChaseLock() {
        vision.chaseLock(true).initialize();
        verify(Subsystems.vision).chaseLock(true);
    }

    @Test
    public void testResetElement() {
        vision.resetElement().initialize();
        verify(Subsystems.vision).resetElement();
    }
}
