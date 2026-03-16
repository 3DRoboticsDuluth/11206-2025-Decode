package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.FWD;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.HOLD;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.REV;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.STOP;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.THRESH;
import static org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem.VEL;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.flywheel;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;
import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class FlywheelSubsystemTests extends TestHarness {
    private static class ConstructorFlywheelSubsystem extends FlywheelSubsystem {
        MotorEx left;
        MotorEx right;

        @Override
        protected MotorEx getMotor(String id, com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA type, org.firstinspires.ftc.robotcore.external.Consumer<MotorEx> consumer) {
            if (left == null) {
                left = mock(MotorEx.class);
                left.motorEx = mock(com.qualcomm.robotcore.hardware.DcMotorEx.class);
            }
            if (right == null) {
                right = mock(MotorEx.class);
                right.motorEx = mock(com.qualcomm.robotcore.hardware.DcMotorEx.class);
            }
            MotorEx motor = "flywheelLeft".equals(id) ? left : right;
            consumer.accept(motor);
            return motor;
        }
    }

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
    public void testPeriodicReturnsWhenUnready() {
        flywheel.errors.add("disabled");
        flywheel.periodic();
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
    public void testHold() {
        VEL = STOP;
        flywheel.hold();
        assert VEL == HOLD;
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

    @Test
    public void testIsReadyHandlesRightMotorBelowThreshold() {
        VEL = FWD;
        when(flywheel.motorLeft.getMaxRPM()).thenReturn(6000.0);
        when(flywheel.motorRight.getMaxRPM()).thenReturn(6000.0);
        when(flywheel.motorLeft.getCPR()).thenReturn(28.0);
        when(flywheel.motorRight.getCPR()).thenReturn(28.0);
        when(flywheel.motorLeft.motorEx.getVelocity()).thenReturn(6000 / 60.0 * 28 * FWD * THRESH);
        when(flywheel.motorRight.motorEx.getVelocity()).thenReturn(0.0);
        assert !flywheel.isReady();
    }

    @Test
    public void testCalculateVelocityAndConfigure() throws Exception {
        Method calculate = FlywheelSubsystem.class.getDeclaredMethod("calculateVelocity");
        calculate.setAccessible(true);

        config.started = false;
        VEL = FWD;
        assert (double) calculate.invoke(flywheel) == FWD;

        config.started = true;
        config.robotCentric = false;
        doReturn(false).when(drive).getGoalLock();
        assert (double) calculate.invoke(flywheel) == FWD;

        config.robotCentric = true;
        when(nav.getGoalDistance()).thenReturn(100.0);
        org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.follower = org.mockito.Mockito.mock(com.pedropathing.follower.Follower.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        when(org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.follower.getVelocity().getXComponent()).thenReturn(1.0);
        when(org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.follower.getAcceleration().getXComponent()).thenReturn(1.0);
        assert (double) calculate.invoke(flywheel) != FWD;

        config.robotCentric = false;
        doReturn(true).when(drive).getGoalLock();
        assert (double) calculate.invoke(flywheel) != FWD;

        Method configure = FlywheelSubsystem.class.getDeclaredMethod("configure", org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx.class, boolean.class);
        configure.setAccessible(true);
        configure.invoke(flywheel, flywheel.motorLeft, true);
        configure.invoke(flywheel, flywheel.motorRight, false);
        verify(flywheel.motorLeft.motorEx).setZeroPowerBehavior(com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT);
        verify(flywheel.motorLeft.motorEx).setDirection(com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE);
        verify(flywheel.motorRight.motorEx).setDirection(com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD);
        verify(flywheel.motorLeft.motorEx).setMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Test
    public void testConstructorInvokesMotorConfigureConsumers() {
        ConstructorFlywheelSubsystem subsystem = new ConstructorFlywheelSubsystem();
        verify(subsystem.left.motorEx).setDirection(com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE);
        verify(subsystem.right.motorEx).setDirection(com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD);
    }
}
