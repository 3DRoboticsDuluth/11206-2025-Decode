package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.config;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.kickstand;
import static org.firstinspires.ftc.teamcode.commands.Commands.sorting;
import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class CommandsTests extends TestHarness {
    @Test
    public void testForward() {
        Commands.initialize();
        assert wait != null;
        assert config != null;
        assert drive != null;
        assert intake != null;
        assert conveyor != null;
        assert sorting != null;
        assert gate != null;
        assert deflector != null;
        assert flywheel != null;
        assert vision != null;
        assert kickstand != null;
        assert auto != null;
    }
}
