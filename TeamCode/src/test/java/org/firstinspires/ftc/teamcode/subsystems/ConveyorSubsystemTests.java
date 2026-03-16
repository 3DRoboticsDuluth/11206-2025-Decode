package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.FWD;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.REV;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.STOP;
import static org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem.VEL;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.conveyor;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ConveyorSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        conveyor = new ConveyorSubsystem() {{
            errors = new ArrayList<>();
            motor = mockMotor();
        }};
    }

    @Test
    public void testPeriodic() {
        VEL = FWD;
        conveyor.periodic();
        verify(conveyor.motor).setVelocityPercentage(VEL);
    }

    @Test
    public void testPeriodicReturnsWhenUnready() {
        conveyor.errors.add("disabled");
        conveyor.periodic();
    }

    @Test
    public void testForward() {
        VEL = STOP;
        conveyor.forward();
        assert VEL == FWD;
    }

    @Test
    public void testReverse() {
        VEL = STOP;
        conveyor.reverse();
        assert VEL == REV;
    }

    @Test
    public void testStopped() {
        VEL = FWD;
        conveyor.stop();
        assert VEL == STOP;
    }

    @Test
    public void testLaunchAndStoppedBranches() throws Exception {
        when(nav.getGoalDistance()).thenReturn(100.0);

        conveyor.launch();
        assert VEL != STOP;
        assert !conveyor.stopped();

        VEL = STOP;
        assert conveyor.stopped();

        Method method = ConveyorSubsystem.class.getDeclaredMethod("calculateVelocity");
        method.setAccessible(true);
        assert (double) method.invoke(conveyor) > 0;
    }

    @Test
    public void testStopWhenGoalLockEnabledKeepsForwardVelocity() {
        config.goalLock = true;
        VEL = FWD;
        conveyor.stop();
        assert VEL == FWD;

        VEL = REV;
        conveyor.stop();
        assert VEL == STOP;
    }

    @Test
    public void testConfigure() throws Exception {
        Method method = ConveyorSubsystem.class.getDeclaredMethod("configure", org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx.class);
        method.setAccessible(true);
        method.invoke(conveyor, conveyor.motor);
        verify(conveyor.motor).stopAndResetEncoder();
        verify(conveyor.motor).setZeroPowerBehavior(com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT);
        verify(conveyor.motor).setRunMode(com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl);
    }
}
