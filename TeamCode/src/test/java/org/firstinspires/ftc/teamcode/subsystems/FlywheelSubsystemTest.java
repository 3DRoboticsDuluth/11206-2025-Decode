package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.util.ArrayList;

public class FlywheelSubsystemTest extends  TestHarness {
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
        assert FlywheelSubsystem.VEL == 0.5;
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
}
