package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.util.ArrayList;

public class FlywheelSubsystemTests extends  TestHarness {
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
        FlywheelSubsystem.VEL = 1;
        flywheel.periodic();
        verify(flywheel.motorLeft.motor).setPower(FlywheelSubsystem.VEL);
        verify(flywheel.motorRight.motor).setPower(FlywheelSubsystem.VEL);
    }

    @Test
    public void testStart() {
        FlywheelSubsystem.VEL = 0;
        flywheel.start();
        assert FlywheelSubsystem.VEL == 0.75;
    }

    @Test
    public void testStop() {
        FlywheelSubsystem.VEL = 1;
        flywheel.stop();
        assert FlywheelSubsystem.VEL == 0;
    }

    @Test
    public void testStopped() {
        FlywheelSubsystem.VEL = 1;
        flywheel.stop();
        assert FlywheelSubsystem.VEL == 0;
    }

    @Test
    public void testReverse() {
        FlywheelSubsystem.VEL = 1;
        flywheel.reverse();
        assert FlywheelSubsystem.VEL == -0.5;
    }

    @Test
    public void testIsReady() {
        FlywheelSubsystem.VEL = 1;
        when(flywheel.motorLeft.getVelocityPercentage()).thenReturn(0.5);
        when(flywheel.motorRight.getVelocityPercentage()).thenReturn(0.5);
        assert !flywheel.isReady();
        when(flywheel.motorLeft.getVelocityPercentage()).thenReturn(0.99);
        when(flywheel.motorRight.getVelocityPercentage()).thenReturn(0.99);
        assert flywheel.isReady();
    }
}
