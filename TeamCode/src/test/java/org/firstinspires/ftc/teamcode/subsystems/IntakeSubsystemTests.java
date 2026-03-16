package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.FWD;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.HOLD;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.REV;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.STOP;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.VEL;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class IntakeSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        intake = new IntakeSubsystem() {{
            errors = new ArrayList<>();
            motor = mockMotor();
        }};
    }

    @Test
    public void testPeriodic() {
        VEL = FWD;
        intake.periodic();
        verify(intake.motor).setVelocityPercentage(VEL);
    }

    @Test
    public void testPeriodicReturnsWhenUnready() {
        intake.errors.add("disabled");
        intake.periodic();
    }

    @Test
    public void testForward() {
        VEL = STOP;
        intake.forward();
        assert VEL == FWD;
    }

    @Test
    public void testReverse() {
        VEL = STOP;
        intake.reverse();
        assert VEL == REV;
    }

    @Test
    public void testStop() {
        VEL = FWD;
        intake.stop();
        assert VEL == STOP;
    }

    @Test
    public void testHold() {
        VEL = STOP;
        intake.hold();
        assert VEL == HOLD;
    }

    @Test
    public void testConfigure() throws Exception {
        Method method = IntakeSubsystem.class.getDeclaredMethod("configure", org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx.class);
        method.setAccessible(true);
        method.invoke(intake, intake.motor);
        verify(intake.motor).setInverted(true);
        verify(intake.motor).stopAndResetEncoder();
        verify(intake.motor).setZeroPowerBehavior(com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT);
        verify(intake.motor).setRunMode(com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl);
    }
}
