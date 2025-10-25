package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class GateCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        gate = new GateCommands();
    }
    
    @Test
    public void testForward() {
        gate.open().initialize();
        verify(Subsystems.gate, times(1)).open();
    }
    
    @Test
    public void testReverse() {
        gate.close().initialize();
        verify(Subsystems.gate, times(1)).close();
    }
}
