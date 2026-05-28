package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.FWD;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.HOLD;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.REV;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.STOP;
import static org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem.VEL;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.util.Debounce;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class IntakeSubsystemTests extends TestHarness {
    private ElapsedTime laserTimer;

    @Override
    public void setUp() {
        super.setUp();
        laserTimer = mock(ElapsedTime.class);
        intake = new IntakeSubsystem() {{
            errors = new ArrayList<>();
            motor = mockMotor();
            laserDebounce = new Debounce(laserTimer);
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
    public void testDetectArtifactUsesLaser2RisingEdge() {
        when(laserTimer.seconds()).thenReturn(IntakeSubsystem.LASER_THRESH);
        setLaser2State(false);
        intake.periodic();

        setLaser2State(true);
        intake.periodic();

        assert intake.artifacts == 1;
        assert !intake.full;
    }

    @Test
    public void testHeldLaser2OnlyCountsOnce() {
        when(laserTimer.seconds()).thenReturn(IntakeSubsystem.LASER_THRESH);
        setLaser2State(false);
        intake.periodic();

        setLaser2State(true);
        intake.periodic();
        intake.periodic();

        assert intake.artifacts == 1;
    }

    @Test
    public void testShortLaserGapDoesNotCountSecondArtifact() {
        when(laserTimer.seconds()).thenReturn(IntakeSubsystem.LASER_THRESH);
        setLaser2State(false);
        intake.periodic();

        setLaser2State(true);
        intake.periodic();

        setLaser2State(false);
        when(laserTimer.seconds()).thenReturn(IntakeSubsystem.LASER_THRESH / 2);
        intake.periodic();

        setLaser2State(true);
        intake.periodic();

        assert intake.artifacts == 1;
    }

    @Test
    public void testLaserClearThresholdRearmsCounter() {
        when(laserTimer.seconds()).thenReturn(IntakeSubsystem.LASER_THRESH);
        setLaser2State(false);
        intake.periodic();

        setLaser2State(true);
        intake.periodic();

        setLaser2State(false);
        when(laserTimer.seconds()).thenReturn(IntakeSubsystem.LASER_THRESH);
        intake.periodic();

        setLaser2State(true);
        intake.periodic();

        assert intake.artifacts == 2;
    }

    @Test
    public void testArtifactCountStopsAtMaxArtifacts() {
        int maxArtifacts = IntakeSubsystem.MAX_ARTIFACTS;
        try {
            IntakeSubsystem.MAX_ARTIFACTS = 2;
            intake.artifacts = 1;

            setLaser2State(false);
            when(laserTimer.seconds()).thenReturn(IntakeSubsystem.LASER_THRESH);
            intake.periodic();

            setLaser2State(true);
            intake.periodic();

            setLaser2State(false);
            intake.periodic();

            setLaser2State(true);
            intake.periodic();

            assert intake.artifacts == 2;
            assert intake.full;
        } finally {
            IntakeSubsystem.MAX_ARTIFACTS = maxArtifacts;
        }
    }

    @Test
    public void testResetClearsCountAndFullState() {
        intake.artifacts = 3;
        intake.full = true;

        intake.reset();

        assert intake.artifacts == 0;
        assert !intake.full;
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

    private void setLaser2State(boolean laser2) {
        when(intake.laser.getState()).thenReturn(laser2);
    }
}
