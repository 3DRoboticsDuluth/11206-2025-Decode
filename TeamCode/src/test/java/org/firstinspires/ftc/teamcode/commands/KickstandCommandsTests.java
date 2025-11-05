package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.kickstand;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;

public class KickstandCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        kickstand = new KickstandCommands();
    }
    
    @Test
    public void testEngage() {
        kickstand.engage().initialize();
        verify(Subsystems.kickstand, times(1)).engage();
    }
    
    @Test
    public void testDisengage() {
        kickstand.disengage().initialize();
        verify(Subsystems.kickstand, times(1)).disengage();
    }
}
