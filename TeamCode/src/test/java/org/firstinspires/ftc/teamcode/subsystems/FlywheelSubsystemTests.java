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
        verify(flywheel.motorLeft.motorEx).setVelocityPIDFCoefficients(FlywheelSubsystem.PIDF.p, FlywheelSubsystem.PIDF.i, FlywheelSubsystem.PIDF.d, FlywheelSubsystem.PIDF.f);
        verify(flywheel.motorRight.motorEx).setVelocityPIDFCoefficients(FlywheelSubsystem.PIDF.p, FlywheelSubsystem.PIDF.i, FlywheelSubsystem.PIDF.d, FlywheelSubsystem.PIDF.f);
        verify(flywheel.motorLeft.motorEx).setVelocity(0);
        verify(flywheel.motorRight.motorEx).setVelocity(0);
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
        when(flywheel.motorLeft.getMaxRPM()).thenReturn(6000.0);
        when(flywheel.motorRight.getMaxRPM()).thenReturn(6000.0);
        when(flywheel.motorLeft.getCPR()).thenReturn(28.0);
        when(flywheel.motorRight.getCPR()).thenReturn(28.0);
        when(flywheel.motorLeft.motorEx.getVelocity()).thenReturn(6000 / 60.0 * 28 * FWD * THRESH / 2);
        when(flywheel.motorRight.motorEx.getVelocity()).thenReturn(6000 / 60.0 * 28 * FWD * THRESH / 2);
        assert !flywheel.isReady();
        when(flywheel.motorLeft.motorEx.getVelocity()).thenReturn(6000 / 60.0 * 28 * FWD * THRESH);
        when(flywheel.motorRight.motorEx.getVelocity()).thenReturn(6000 / 60.0 * 28 * FWD * THRESH);
        assert flywheel.isReady();
    }
}
