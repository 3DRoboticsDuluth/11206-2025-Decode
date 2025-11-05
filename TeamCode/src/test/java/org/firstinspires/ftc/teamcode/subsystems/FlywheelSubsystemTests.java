package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.FWD;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.REV;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.STOP;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.THRESH;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.VEL;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.util.ArrayList;

public class FlywheelSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        flywheel = new FlywheelSubsystem() {{
            errors = new ArrayList<>();
            motorLeft = mockMotor();
            motorRight = mockMotor();
        }};
    }

    @Test
    public void testPeriodic() {
        VEL = FWD;
        flywheel.periodic();
        verify(flywheel.motorLeft.motor).setPower(VEL);
        verify(flywheel.motorRight.motor).setPower(VEL);
    }

    @Test
    public void testForward() {
        VEL = STOP;
        flywheel.forward();
        assert VEL == FWD;
    }

    @Test
    public void testReverse() {
        VEL = FWD;
        flywheel.reverse();
        assert VEL == REV;
    }

    @Test
    public void testStop() {
        VEL = FWD;
        flywheel.stop();
        assert VEL == STOP;
    }

    @Test
    public void testIsReady() {
        VEL = FWD;
        when(flywheel.motorLeft.getVelocityPercentage()).thenReturn(FWD * THRESH / 2);
        when(flywheel.motorRight.getVelocityPercentage()).thenReturn(FWD * THRESH / 2);
        assert !flywheel.isReady();
        when(flywheel.motorLeft.getVelocityPercentage()).thenReturn(FWD * THRESH);
        when(flywheel.motorRight.getVelocityPercentage()).thenReturn(FWD * THRESH);
        assert flywheel.isReady();
    }
}
