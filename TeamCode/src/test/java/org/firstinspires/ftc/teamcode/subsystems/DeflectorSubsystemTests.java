package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.INC;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.MAX;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.MID;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.MIN;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.POS;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.junit.Test;

import java.util.ArrayList;

public class DeflectorSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        deflector = new DeflectorSubsystem() {{
            errors = new ArrayList<>();
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testPeriodic() {
        POS = MAX;
        deflector.periodic();
        verify(deflector.servo).setPosition(POS);
    }

    @Test
    public void testUp() {
        POS = MID;
        deflector.up();
        assert POS == MID + INC;
    }

    @Test
    public void testDown() {
        POS = MID;
        deflector.down();
        assert POS == MID - INC;
    }

    @Test
    public void testCompensate() {
        POS = MIN;
        deflector.compensate();
        assert POS == MID;
    }
}
