package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.sorting;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class SortingCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        sorting = new SortingCommands();
    }
    
    @Test
    public void testSort() {
        sorting.sort().initialize();
        verify(Subsystems.sorting, times(1)).sort();
    }
    
    @Test
    public void testPass() {
        sorting.pass().initialize();
        verify(Subsystems.sorting, times(1)).pass();
    }
}
