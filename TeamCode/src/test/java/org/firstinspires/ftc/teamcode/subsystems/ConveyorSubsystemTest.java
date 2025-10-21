package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.conveyor;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.util.ArrayList;

public class ConveyorSubsystemTest extends  TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        conveyor = new ConveyorSubsystem() {{
            errors = new ArrayList<>();
            motor = mockMotor();
        }};
    }

    @Test
    public void testForward() {
        ConveyorSubsystem.VEL = 0;
        conveyor.forward();
        assert ConveyorSubsystem.VEL == 0.5;
    }

    @Test
    public void testReverse() {
        ConveyorSubsystem.VEL = 0;
        conveyor.reverse();
        assert ConveyorSubsystem.VEL == -0.25;
    }

    @Test
    public void testStopped() {
        ConveyorSubsystem.VEL = 1;
        conveyor.stop();
        assert ConveyorSubsystem.VEL == 0;
    }

    @Test
    public void testPeriodic() {
        ConveyorSubsystem.VEL = 1;
        conveyor.periodic();
        verify(conveyor.motor).setVelocityPercentage(
            ConveyorSubsystem.VEL
        );
    }
}
