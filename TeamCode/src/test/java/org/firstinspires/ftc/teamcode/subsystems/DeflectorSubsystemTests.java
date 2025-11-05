package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.junit.Test;

public class DeflectorSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        deflector = new DeflectorSubsystem() {{
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testPeriodic() {
        DeflectorSubsystem.POS = 1;
        deflector.periodic();
        verify(deflector.servo).setPosition(
            DeflectorSubsystem.POS
        );
    }

    @Test
    public void testUp() {
        double startPos = 0.5;
        DeflectorSubsystem.POS = startPos;
        deflector.up();
        assert DeflectorSubsystem.POS == startPos + DeflectorSubsystem.INC;
    }

    @Test
    public void testDown() {
        double startPos = 0.5;
        DeflectorSubsystem.POS = startPos;
        deflector.down();
        assert DeflectorSubsystem.POS == startPos - DeflectorSubsystem.INC;
    }

    @Test
    public void testCompensate() {
        DeflectorSubsystem.POS = 0;
        deflector.compensate();
        assert DeflectorSubsystem.POS == DeflectorSubsystem.MID;
    }
}
