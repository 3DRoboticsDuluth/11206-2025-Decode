package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.mockito.Mockito.verify;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class DeflectorSubsystemTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        deflector = new DeflectorSubsystem() {{
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testSetPosition() {
        DeflectorSubsystem.POSITION = 0;
        deflector.setPosition(1);
        assert DeflectorSubsystem.POSITION == 1;
    }

    @Test
    public void testPeriodic() {
        DeflectorSubsystem.POSITION = 1;
        deflector.periodic();
        verify(deflector.servo).setPosition(
            DeflectorSubsystem.POSITION
        );
    }
}
