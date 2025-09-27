package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.config;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class ConfigCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        config = new ConfigCommands();
    }
    
    @Test
    public void testSetEditable() {
        boolean editable = true;
        config.setEditable(editable).initialize();
        verify(Subsystems.config, times(1)).setEditable(editable);
    }
    
    @Test
    public void testChangeItem() {
        ConfigSubsystem.Change change = ConfigSubsystem.Change.NEXT;
        config.changeItem(change).initialize();
        verify(Subsystems.config, times(1)).changeItem(change);
    }
    
    @Test
    public void testChangeValue() {
        ConfigSubsystem.Change change = ConfigSubsystem.Change.NEXT;
        config.changeValue(change).initialize();
        verify(Subsystems.config, times(1)).changeValue(change);
    }
}
