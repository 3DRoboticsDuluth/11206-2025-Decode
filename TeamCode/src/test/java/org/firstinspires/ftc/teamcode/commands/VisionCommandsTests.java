package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.mockito.Mockito.spy;

import org.firstinspires.ftc.teamcode.TestHarness;

public class VisionCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        vision = spy(new VisionCommands());
    }
}
