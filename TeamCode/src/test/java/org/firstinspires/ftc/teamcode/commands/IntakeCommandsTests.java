package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class IntakeCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        intake = new IntakeCommands();
    }
    
    @Test
    public void testForward() {
        intake.forward().initialize();
        verify(Subsystems.intake, times(1)).forward();
    }
    
    @Test
    public void testReverse() {
        intake.reverse().initialize();
        verify(Subsystems.intake, times(1)).reverse();
    }
    
    @Test
    public void testStop() {
        intake.stop().initialize();
        verify(Subsystems.intake, times(1)).stop();
    }
}
