package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.hardware;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem.PIPELINE;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class VisionSubsystemsTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        vision = new VisionSubsystem();
    }

    @Test
    public void testSwitchPipeline() {
        assert PIPELINE == 0;
        if (hardware.limelight != null)
            vision.switchPipeline(1, true);
        verify(hardware.limelight).pipelineSwitch(1);
        assert PIPELINE == 1;
    }
}
