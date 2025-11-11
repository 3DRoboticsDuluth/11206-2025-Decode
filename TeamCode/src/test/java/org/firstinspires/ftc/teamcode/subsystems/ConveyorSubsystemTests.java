package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.FWD;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.LAUNCH;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.REV;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.STOP;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.VEL;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.conveyor;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.util.ArrayList;

public class ConveyorSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        conveyor = new ConveyorSubsystem() {{
            errors = new ArrayList<>();
            motor = mockMotor();
        }};
    }

    @Test
    public void testPeriodic() {
        VEL = FWD;
        conveyor.periodic();
        verify(conveyor.motor).setVelocityPercentage(VEL);
    }

    @Test
    public void testLaunch() {
        VEL = STOP;
        conveyor.launch();
        assert VEL == LAUNCH;
    }

    @Test
    public void testForward() {
        VEL = STOP;
        conveyor.forward();
        assert VEL == FWD;
    }

    @Test
    public void testReverse() {
        VEL = STOP;
        conveyor.reverse();
        assert VEL == REV;
    }

    @Test
    public void testStopped() {
        VEL = FWD;
        conveyor.stop();
        assert VEL == STOP;
    }
}
