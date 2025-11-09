package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.GateSubsystem.CLOSE;
import static org.firstinspires.ftc.teamcode.subsystems.GateSubsystem.OPEN;
import static org.firstinspires.ftc.teamcode.subsystems.GateSubsystem.POS;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.gate;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.junit.Test;

import java.util.ArrayList;

public class GateSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        gate = new GateSubsystem() {{
            errors = new ArrayList<>();
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testPeriodic() {
        POS = OPEN;
        gate.periodic();
        verify(gate.servo).setPosition(POS);
    }

    @Test
    public void testOpen() {
        POS = CLOSE;
        gate.open();
        assert POS == OPEN;
    }

    @Test
    public void testClose() {
        POS = OPEN;
        gate.close();
        assert POS == CLOSE;
    }
}
