package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;
import static org.mockito.Mockito.verify;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class IntakeSubsystemTest extends  TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        intake = new IntakeSubsystem() {{
            motor = mockMotor();
        }};
    }

    @Test
    public void testForward() {
        IntakeSubsystem.VELOCITY = 0;
        intake.forward();
        assert IntakeSubsystem.VELOCITY == 1;
    }

    @Test
    public void testReverse() {
        IntakeSubsystem.VELOCITY = -0;
        intake.reverse();
        assert  IntakeSubsystem.VELOCITY == -1;
    }

    @Test
    public void testStopped() {
        IntakeSubsystem.VELOCITY = 1;
        intake.stop();
        assert IntakeSubsystem.VELOCITY == 0;
    }

    @Test
    public void testPeriodic() {
        IntakeSubsystem.VELOCITY = 1;
        intake.periodic();
        verify(intake.motor).setVelocity(
                IntakeSubsystem.VELOCITY
        );
    }
}