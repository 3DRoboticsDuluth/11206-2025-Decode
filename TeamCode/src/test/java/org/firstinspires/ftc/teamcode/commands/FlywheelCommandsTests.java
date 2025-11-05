package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;

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
        verify(Subsystems.flywheel).start();
    }

    @Test
    public void testStop() {
        flywheel.stop().initialize();
        verify(Subsystems.flywheel).stop();
    }

    @Test
    public void testReverse() {
        flywheel.reverse().initialize();
        verify(Subsystems.flywheel).reverse();
    }

    @Test
    public void testIsReady() {
        wait = new WaitCommands();
        flywheel.isReady().isFinished();
        verify(Subsystems.flywheel).isReady();
    }
}
