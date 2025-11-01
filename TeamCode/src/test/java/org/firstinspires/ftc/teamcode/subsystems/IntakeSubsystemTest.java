package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.util.ArrayList;

public class IntakeSubsystemTest extends  TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        intake = new IntakeSubsystem() {{
            errors = new ArrayList<>();
            motor = mockMotor();
        }};
    }

    @Test
    public void testForward() {
        IntakeSubsystem.VEL = 0;
        intake.forward();
        assert IntakeSubsystem.VEL == 0.25;
    }

    @Test
    public void testReverse() {
        IntakeSubsystem.VEL = 0;
        intake.reverse();
        assert IntakeSubsystem.VEL == -0.25;
    }

    @Test
    public void testStopped() {
        IntakeSubsystem.VEL = 1;
        intake.stop();
        assert IntakeSubsystem.VEL == 0;
    }

    @Test
    public void testPeriodic() {
        IntakeSubsystem.VEL = 1;
        intake.periodic();
        verify(intake.motor).setVelocityPercentage(
            IntakeSubsystem.VEL
        );
    }
}
