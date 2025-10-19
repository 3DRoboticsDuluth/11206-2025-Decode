package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;

import org.firstinspires.ftc.teamcode.TestHarness;

public class VisionSubsystemsTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        vision = new VisionSubsystem();
    }
}
