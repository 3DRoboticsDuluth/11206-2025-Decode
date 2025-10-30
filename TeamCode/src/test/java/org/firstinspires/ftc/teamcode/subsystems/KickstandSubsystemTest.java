package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.gate;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.kickstand;
import static org.mockito.Mockito.verify;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.nio.file.WatchEvent;

public class KickstandSubsystemTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        kickstand = new KickstandSubsystem() {{
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testPeriodic() {
        KickstandSubsystem.POS = 1;
        kickstand.periodic();
        verify(kickstand.servo).setPosition(
            KickstandSubsystem.POS
        );
    }

    @Test
    public void testEngage() {
        KickstandSubsystem.POS = KickstandSubsystem.DISENGAGE;
        kickstand.engage();
        assert KickstandSubsystem.POS == KickstandSubsystem.ENGAGE;
    }

    @Test
    public void testDisengage() {
        KickstandSubsystem.POS = KickstandSubsystem.ENGAGE;
        kickstand.disengage();
        assert KickstandSubsystem.POS == KickstandSubsystem.DISENGAGE;
    }
}
