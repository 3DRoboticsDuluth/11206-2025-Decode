package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class ConveyorCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        conveyor = new ConveyorCommands();
    }

    @Test
    public void testLaunch() {
        conveyor.launch().initialize();
        verify(Subsystems.conveyor, times(1)).launch();
    }

    @Test
    public void testForward() {
        conveyor.forward().initialize();
        verify(Subsystems.conveyor, times(1)).forward();
    }

    @Test
    public void testReverse() {
        conveyor.reverse().initialize();
        verify(Subsystems.conveyor, times(1)).reverse();
    }
    
    @Test
    public void testStop() {
        conveyor.stop().initialize();
        verify(Subsystems.conveyor, times(1)).stop();
    }
}
