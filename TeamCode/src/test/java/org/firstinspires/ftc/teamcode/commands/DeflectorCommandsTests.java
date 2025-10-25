package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class DeflectorCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        deflector = new DeflectorCommands();
    }
    
    @Test
    public void testUp() {
        deflector.up().initialize();
        verify(Subsystems.deflector, times(1)).up();
    }
    
    @Test
    public void testDown() {
        deflector.down().initialize();
        verify(Subsystems.deflector, times(1)).down();
    }
}
