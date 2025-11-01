package org.firstinspires.ftc.teamcode.controls;

import static org.firstinspires.ftc.teamcode.commands.Commands.kickstand;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class KickstandControlTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        new KickstandControl();
    }

    @Test
    public void testLeftAndRightBumper() {
        input(() -> gamepad2.gamepad.left_bumper = gamepad2.gamepad.right_bumper = true);
        verify(kickstand).engage();
    }
}
