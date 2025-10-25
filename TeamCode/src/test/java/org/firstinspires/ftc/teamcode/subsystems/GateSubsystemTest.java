package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.gate;
import static org.mockito.Mockito.verify;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class GateSubsystemTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        gate = new GateSubsystem() {{
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testPeriodic() {
        GateSubsystem.POS = 1;
        gate.periodic();
        verify(gate.servo).setPosition(
            GateSubsystem.POS
        );
    }

    @Test
    public void testOpen() {
        GateSubsystem.POS = GateSubsystem.MIN;
        gate.open();
        assert GateSubsystem.POS == GateSubsystem.MAX;
    }

    @Test
    public void testClose() {
        GateSubsystem.POS = GateSubsystem.MAX;
        gate.close();
        assert GateSubsystem.POS == GateSubsystem.MIN;
    }
}
