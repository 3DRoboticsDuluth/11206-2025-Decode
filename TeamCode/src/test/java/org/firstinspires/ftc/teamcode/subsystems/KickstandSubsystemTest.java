package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.KickstandSubsystem.DISENGAGE;
import static org.firstinspires.ftc.teamcode.subsystems.KickstandSubsystem.ENGAGE;
import static org.firstinspires.ftc.teamcode.subsystems.KickstandSubsystem.POS;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.kickstand;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;
import org.junit.Test;

import java.util.ArrayList;

public class KickstandSubsystemTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        kickstand = new KickstandSubsystem() {{
            errors = new ArrayList<>();
            servo = mockDevice(ServoEx.class);
        }};
    }

    @Test
    public void testPeriodic() {
        POS = ENGAGE;
        kickstand.periodic();
        verify(kickstand.servo).set(POS);
    }

    @Test
    public void testEngage() {
        POS = DISENGAGE;
        kickstand.engage();
        assert POS == ENGAGE;
    }

    @Test
    public void testDisengage() {
        POS = ENGAGE;
        kickstand.disengage();
        assert POS == DISENGAGE;
    }
}
