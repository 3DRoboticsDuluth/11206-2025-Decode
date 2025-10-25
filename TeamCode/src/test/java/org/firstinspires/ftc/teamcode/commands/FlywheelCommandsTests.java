package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class FlywheelCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        flywheel = new FlywheelCommands();
    }
    
    @Test
    public void testStart() {
        flywheel.start().initialize();
        verify(Subsystems.flywheel, times(1)).start();
    }

    @Test
    public void testStop() {
        flywheel.stop().initialize();
        verify(Subsystems.flywheel, times(1)).stop();
    }
}
