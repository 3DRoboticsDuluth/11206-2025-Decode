package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.config;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.conveyor;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.gate;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.kickstand;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.sorting;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.timing;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class SubsystemsTests extends TestHarness {
    @Test
    public void testInitialize() {
        Subsystems.initialize();
        assert config != null;
        assert nav != null;
        assert drive != null;
        assert intake != null;
        assert conveyor != null;
        assert sorting != null;
        assert gate != null;
        assert deflector != null;
        assert flywheel != null;
        assert vision != null;
        assert kickstand != null;
        assert timing != null;
    }
}
