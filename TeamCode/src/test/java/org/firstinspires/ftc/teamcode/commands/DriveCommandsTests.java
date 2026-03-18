package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_AUTO;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_HIGH;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_INTAKE;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_LOW;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.POWER_MEDIUM;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.TO_FAR;
import static org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.follower;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/** @noinspection unchecked, DataFlowIssue */
public class DriveCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        config.alliance = RED;
        nav = spy(new NavSubsystem());
        drive = spy(new DriveCommands());
        PathBuilder pathBuilder = mock(PathBuilder.class, RETURNS_SELF);
        doAnswer(invocation -> {
            invocation.<Consumer<PathBuilder>>getArgument(0).accept(pathBuilder);
            return wait.noop();
        }).when(drive).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), anyBoolean());
        doReturn(wait.noop())
            .when(drive)
            .untilDistance(anyDouble());
    }

    @Test
    public void testSetPowerLow() {
        drive.setPowerLow().initialize();
        verify(follower).setMaxPower(POWER_LOW);
    }

    @Test
    public void testSetPowerMedium() {
        drive.setPowerMedium().initialize();
        verify(follower).setMaxPower(POWER_MEDIUM);
    }

    @Test
    public void testSetPowerHigh() {
        drive.setPowerHigh().initialize();
        verify(follower).setMaxPower(POWER_HIGH);
    }

    @Test
    public void testSetPowerIntake() {
        drive.setPowerIntake().initialize();
        verify(follower).setMaxPower(POWER_INTAKE);
    }

    @Test
    public void testSetPowerAuto() {
        drive.setPowerAuto().initialize();
        verify(follower).setMaxPower(POWER_AUTO);
    }

    @Test
    public void testInput() {
        drive.input(() -> 1.0, () -> 2.0, () -> 3.0).execute();
        verify(org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive).inputs(1.0, 2.0, 3.0);
    }

    @Test
    public void testToStart() {
        drive.toStart().initialize();
        verify(nav).getStartPose();
    }

    @Test
    public void testToSpike0CoversNearAndFarBranches() {
        config.pose.x = 2;
        drive.toSpike0().initialize();

        config.pose.x = 0;
        drive.toSpike0().initialize();

        verify(nav, times(6)).getSpike0();
    }

    @Test
    public void testToSpike1CoversXAndYBranches() {
        config.pose.x = 2;
        config.pose.y = 3 * TILE_WIDTH;
        drive.toSpike1().initialize();

        config.pose.x = 2;
        config.pose.y = 0;
        drive.toSpike1().initialize();

        config.pose.x = 0;
        config.pose.y = 3 * TILE_WIDTH;
        drive.toSpike1().initialize();

        config.pose.x = 0;
        config.pose.y = 0;
        drive.toSpike1().initialize();

        verify(nav, times(8)).getSpike1();
    }

    @Test
    public void testToSpike2CoversXAndYBranches() {
        config.pose.x = 2;
        config.pose.y = 3 * TILE_WIDTH;
        drive.toSpike2().initialize();

        config.pose.x = 2;
        config.pose.y = 0;
        drive.toSpike2().initialize();

        config.pose.x = 0;
        config.pose.y = 3 * TILE_WIDTH;
        drive.toSpike2().initialize();

        config.pose.x = 0;
        config.pose.y = 0;
        drive.toSpike2().initialize();

        verify(nav, times(8)).getSpike2();
    }

    @Test
    public void testToSpike3CoversXAndYBranches() {
        config.pose.x = 2;
        config.pose.y = 3 * TILE_WIDTH;
        drive.toSpike3().initialize();

        config.pose.x = 2;
        config.pose.y = 0;
        drive.toSpike3().initialize();

        config.pose.x = 0;
        config.pose.y = 3 * TILE_WIDTH;
        drive.toSpike3().initialize();

        config.pose.x = 0;
        config.pose.y = 0;
        drive.toSpike3().initialize();

        verify(nav, times(8)).getSpike3();
    }

    @Test
    public void testToLaunchNear() {
        drive.toDepositSouth(0, 0).initialize();
        verify(nav).getDepositSouthPose(0, 0);
    }

    @Test
    public void testToLaunchFar() {
        config.pose.x = -2 * TILE_WIDTH;
        drive.toDepositSouth(0, 0).initialize();
        verify(nav, times(2)).getDepositSouthPose(0, 0);
    }

    @Test
    public void testToLaunchNorth() {
        drive.toDepositNorth(0, 0).initialize();
        verify(nav).getDepositNorthPose(0, 0);
    }

    @Test
    public void testToGate() {
        drive.toGate().initialize();
        verify(nav, times(2)).getGatePose();
    }

    @Test
    public void testToGateIntake() {
        drive.toGateIntake().initialize();
        verify(nav, times(2)).getGateIntakePose();
    }

    @Test
    public void testToGateIntakeDepart() {
        drive.toGateIntakeDepart().initialize();
        verify(nav, times(2)).getGateIntakeDepartPose();
    }

    @Test
    public void testBase() {
        drive.toBase().initialize();
        verify(nav).getBasePose();
    }

    @Test
    public void testParking() {
        drive.toParking(true, NavSubsystem.Axial.CENTER, NavSubsystem.Lateral.CENTER).initialize();
        verify(nav).getParkingPose(true, NavSubsystem.Axial.CENTER, NavSubsystem.Lateral.CENTER);
    }

    @Test
    public void testHold() {
        drive.hold().initialize();
        verify(follower).holdPoint(any());
    }

    @Test
    public void testStop() {
        drive.stop().initialize();
        verify(follower).startTeleOpDrive();
        verify(follower).setTeleOpDrive(0, 0, 0, 0);
    }

    @Test
    public void testGoalLock() {
        drive.goalLock(true).initialize();
        verify(org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive).setGoalLock(true);
    }

    @Test
    public void testChaseLock() {
        drive.chaseLock(true).initialize();
        verify(org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive).setChaseLock(true);
    }

    @Test
    public void testToChaseScan() {
        drive.toChaseScan().initialize();
        verify(nav).getChaseScanPose();
    }

    @Test
    public void testToChase() {
        drive.toChase(2).initialize();
        verify(nav, times(2)).getChasePose(2);
    }

    @Test
    public void testChase() {
        config.pose = new Pose(100, 100, 0);
        vision.element = new Pose(1, 1, 0);

        assert !drive.chase().isFinished();
    }

    @Test
    public void testUntilDistanceHandlesPositiveAndNegativeTargets() {
        DriveCommands subject = new DriveCommands();

        subject.untilDistance(1).initialize();
        subject.untilDistance(-1).initialize();

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait, times(2)).until(captor.capture());

        when(follower.getDistanceTraveledOnPath()).thenReturn(0.5, 1.5);
        assert !captor.getAllValues().get(0).getAsBoolean();
        assert captor.getAllValues().get(0).getAsBoolean();

        when(follower.getDistanceRemaining()).thenReturn(2.0, 0.5);
        assert !captor.getAllValues().get(1).getAsBoolean();
        assert captor.getAllValues().get(1).getAsBoolean();
    }

    @Test
    public void testUntilPathCompletionHandlesPositiveAndNegativeTargets() {
        DriveCommands subject = new DriveCommands();

        subject.untilPathCompletion(0.5).initialize();
        subject.untilPathCompletion(-0.2).initialize();

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait, times(2)).until(captor.capture());

        when(follower.getPathCompletion()).thenReturn(0.25, 0.75, 0.9, 0.7);
        assert !captor.getAllValues().get(0).getAsBoolean();
        assert captor.getAllValues().get(0).getAsBoolean();
        assert !captor.getAllValues().get(1).getAsBoolean();
        assert captor.getAllValues().get(1).getAsBoolean();
    }

    @Test
    public void testUntilTValueHandlesPositiveAndNegativeTargets() {
        DriveCommands subject = new DriveCommands();

        subject.untilTValue(0.5).initialize();
        subject.untilTValue(-0.2).initialize();

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait, times(2)).until(captor.capture());

        when(follower.getCurrentTValue()).thenReturn(0.25, 0.75, 0.9, 0.7);
        assert !captor.getAllValues().get(0).getAsBoolean();
        assert captor.getAllValues().get(0).getAsBoolean();
        assert !captor.getAllValues().get(1).getAsBoolean();
        assert captor.getAllValues().get(1).getAsBoolean();
    }

    @Test
    public void testUntilHeadingConditionHandlesTrueAndFalse() {
        DriveCommands subject = new DriveCommands();
        subject.untilHeading(15);

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait).until(captor.capture());

        when(nav.getGoalHeadingRemaining()).thenReturn(0.5);
        assert !captor.getValue().getAsBoolean();

        when(nav.getGoalHeadingRemaining()).thenReturn(0.01);
        assert captor.getValue().getAsBoolean();
    }

    @Test
    public void testUntilNotBusyConditionHandlesTrueAndFalse() {
        DriveCommands subject = spy(new DriveCommands());
        org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive = mock(org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem.class);

        subject.untilNotBusy().initialize();

        ArgumentCaptor<BooleanSupplier> captor = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(wait).until(captor.capture());

        when(org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive.isBusy()).thenReturn(true);
        assert !captor.getValue().getAsBoolean();

        when(org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive.isBusy()).thenReturn(false);
        assert captor.getValue().getAsBoolean();
    }

    @Test
    public void testIsToFarCoversGuardClausesAndDistanceThreshold() {
        DriveCommands subject = new DriveCommands();

        config.teleop = false;
        assert !subject.isToFar(new Pose(100, 0, 0));

        config.teleop = true;
        config.pose = null;
        assert !subject.isToFar(new Pose(100, 0, 0));

        config.pose = new Pose(0, 0, 0);
        assert !subject.isToFar(null);
        assert !subject.isToFar(new Pose(1, 0, 0));
        assert subject.isToFar(new Pose(TO_FAR + 5, 0, 0));
    }

    @Test
    public void testRumble() {
        drive.rumble().initialize();
        drive.rumble1().initialize();
        drive.rumble2().initialize();
        drive.rumble1(0.5, 0.25).initialize();
        drive.rumble2(0.5, 0.25).initialize();
        verify(gamepad1.gamepad, times(6)).rumble(anyDouble(), anyDouble(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    public void testForwardStrafeAndTurn() {
        DriveCommands subject = spy(new DriveCommands());
        doReturn(wait.noop()).when(subject).to(any(Pose.class));

        subject.forward(5).initialize();
        subject.strafe(5).initialize();
        subject.turn(90).initialize();

        verify(subject, times(3)).to(any(Pose.class));
    }

    @Test
    public void testToCoordinateOverloads() {
        DriveCommands subject = spy(new DriveCommands());
        doReturn(wait.noop()).when(subject).to(any(Pose.class));

        subject.to(1, 2, 90);
        subject.to(1, 2, 90, true);

        ArgumentCaptor<Pose> captor = ArgumentCaptor.forClass(Pose.class);
        verify(subject, times(2)).to(captor.capture());
        assert captor.getAllValues().get(0).hold == Pose.HOLD_DEFAULT;
        assert captor.getAllValues().get(1).hold;
    }

    @Test
    public void testToHonorsReverseFlag() {
        DriveCommands subject = spy(new DriveCommands());
        PathBuilder builder = mock(PathBuilder.class, RETURNS_SELF);
        doAnswer(invocation -> {
            invocation.<Consumer<PathBuilder>>getArgument(0).accept(builder);
            return wait.noop();
        }).when(subject).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), anyBoolean());

        subject.to(new Pose(1, 1, 0)).initialize();
        subject.reverse().initialize();
        subject.to(new Pose(2, 2, 0)).initialize();

        verify(builder, times(1)).setReversed();
    }

    @Test
    public void testToInitializesCapturedBezierCurve() {
        DriveCommands subject = new DriveCommands();
        PathBuilder builder = mock(PathBuilder.class, RETURNS_SELF);
        ArgumentCaptor<BezierCurve> captor = ArgumentCaptor.forClass(BezierCurve.class);
        when(follower.pathBuilder()).thenReturn(builder);
        when(builder.addPath(captor.capture())).thenReturn(builder);

        subject.to(new Pose(1, 1, 0, false)).initialize();
        captor.getValue().initialize();

        assert captor.getValue().getControlPoints().size() == 3;
    }

    @Test
    public void testToCurveCurvesAndPathsHonorHoldOnEndPose() {
        DriveCommands subject = spy(new DriveCommands());
        doReturn(wait.noop()).when(subject).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), anyBoolean());

        subject.to(new Pose(1, 1, 0, true)).initialize();
        subject.curve().initialize();
        subject.curve(new Pose(1, 0, 0), new Pose(2, 0, 0, true)).initialize();
        subject.curves(new Pose(1, 0, 0, true)).initialize();
        subject.curve(new Pose(3, 0, 0, false)).initialize();
        subject.curves(new Pose(4, 0, 0, false)).initialize();
        subject.paths(path -> {}).initialize();

        verify(subject, times(3)).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), org.mockito.ArgumentMatchers.eq(true));
        verify(subject, times(4)).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), org.mockito.ArgumentMatchers.eq(false));
    }

    @Test
    public void testCurveHandlesMidpointInsertionAndReverse() {
        DriveCommands subject = spy(new DriveCommands());
        PathBuilder builder = mock(PathBuilder.class, RETURNS_SELF);
        doAnswer(invocation -> {
            invocation.<Consumer<PathBuilder>>getArgument(0).accept(builder);
            return wait.noop();
        }).when(subject).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), anyBoolean());

        subject.curve(new Pose(1, 0, 0), new Pose(2, 0, 0)).initialize();
        subject.reverse().initialize();
        subject.curve(new Pose(1, 0, 0), new Pose(2, 0, 0), new Pose(3, 0, 0)).initialize();

        verify(builder, times(1)).setReversed();
    }

    @Test
    public void testCurvesHandlesEmptyAndPopulatedPaths() {
        DriveCommands subject = spy(new DriveCommands());
        PathBuilder builder = mock(PathBuilder.class, RETURNS_SELF);
        doAnswer(invocation -> {
            invocation.<Consumer<PathBuilder>>getArgument(0).accept(builder);
            return wait.noop();
        }).when(subject).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), anyBoolean());

        subject.curves().initialize();
        subject.forward().initialize();
        subject.curves(new Pose(1, 0, 0)).initialize();
        subject.reverse().initialize();
        subject.curves(new Pose(1, 0, 0)).initialize();

        verify(builder, times(1)).setReversed();
    }

    @Test
    public void testPathsHandlesEmptyAndPopulatedConsumers() {
        DriveCommands subject = spy(new DriveCommands());
        AtomicInteger count = new AtomicInteger();
        PathBuilder builder = mock(PathBuilder.class, RETURNS_SELF);
        doAnswer(invocation -> {
            invocation.<Consumer<PathBuilder>>getArgument(0).accept(builder);
            return wait.noop();
        }).when(subject).follow(org.mockito.ArgumentMatchers.<Consumer<PathBuilder>>any(), anyBoolean());

        subject.paths().initialize();
        subject.paths(path -> count.incrementAndGet()).initialize();

        assert count.get() == 1;
    }

    @Test
    public void testFollowConsumerBuildsPathChain() {
        DriveCommands subject = spy(new DriveCommands());
        PathBuilder builder = mock(PathBuilder.class, RETURNS_SELF);
        PathChain pathChain = mock(PathChain.class);
        when(follower.pathBuilder()).thenReturn(builder);
        when(builder.build()).thenReturn(pathChain);
        doReturn(wait.noop()).when(subject).follow(pathChain, true);

        subject.follow(path -> {}, true);

        verify(subject).follow(pathChain, true);
    }

    @Test
    public void testFollowPathChainCallsControlsReset() {
        DriveCommands subject = spy(new DriveCommands());
        PathChain pathChain = mock(PathChain.class);
        doReturn(wait.noop()).when(subject).controlsReset();

        subject.follow(pathChain, true);

        verify(subject).controlsReset();
    }

    @Test
    public void testControlsReset() {
        org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive.controlsReset = true;
        drive.controlsReset().initialize();
        assert !org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive.controlsReset;
    }

    @Test
    public void testGetPoseHonorsReverseFlag() throws Exception {
        DriveCommands subject = new DriveCommands();
        config.pose = new Pose(2, 3, 0.5);

        Method getPose = DriveCommands.class.getDeclaredMethod("getPose");
        getPose.setAccessible(true);

        assert getPose.invoke(subject) == config.pose;

        subject.reverse().initialize();
        Pose reversed = (Pose) getPose.invoke(subject);
        assert reversed.x != config.pose.x || reversed.y != config.pose.y || reversed.heading != config.pose.heading;
    }

    @Test
    public void testCompareCoversAllBranches() throws Exception {
        Method compare = DriveCommands.class.getDeclaredMethod("compare", Pose.class, Pose.class, boolean.class);
        compare.setAccessible(true);

        assert !(Boolean) compare.invoke(null, new Pose(0, 0, 0), new Pose(5, 0, 0), true);
        assert !(Boolean) compare.invoke(null, new Pose(0, 0, 0), new Pose(0, 5, 0), true);
        assert !(Boolean) compare.invoke(null, new Pose(0, 0, 0), new Pose(0, 0, 2), true);
        assert (Boolean) compare.invoke(null, new Pose(0, 0, 0), new Pose(0.5, 0.5, 0.01), true);
        assert (Boolean) compare.invoke(null, new Pose(0, 0, 0), new Pose(0.5, 0.5, 2), false);
    }
}
