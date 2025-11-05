package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.FWD;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.REV;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.STOP;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.VEL;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.util.ArrayList;

public class IntakeSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        intake = new IntakeSubsystem() {{
            errors = new ArrayList<>();
            motor = mockMotor();
        }};
    }

    @Test
    public void testPeriodic() {
        VEL = FWD;
        intake.periodic();
        verify(intake.motor).setVelocityPercentage(VEL);
    }

    @Test
    public void testForward() {
        VEL = STOP;
        intake.forward();
        assert VEL == FWD;
    }

    @Test
    public void testReverse() {
        VEL = STOP;
        intake.reverse();
        assert VEL == REV;
    }

    @Test
    public void testStop() {
        VEL = FWD;
        intake.stop();
        assert VEL == STOP;
    }
}
