package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Alliance.BLUE;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;
import org.junit.Assume;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class NavSubsystemTests extends  TestHarness {
    @DataPoints
    public static Alliance[] alliances = { RED, BLUE };

    @DataPoints
    public static Side[] sides = { NORTH, SOUTH };

    @Override
    public void setUp() {
        super.setUp();
        nav = new NavSubsystem();
    }

    @Theory
    public void testGetLaunchNearPose(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);
        config.alliance = alliance;
        config.side = side;

        Pose expected = new Pose(
            -1.0 * TILE_WIDTH,
            config.alliance.sign * -0.75 * TILE_WIDTH,
            toRadians(config.alliance.sign * 53.8)
        );

        Pose actual = nav.getDepositSouthPose(0, 0);

        comparePose(expected, actual);
    }

    @Theory
    public void testGetLaunchFarPose(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);
        config.alliance = alliance;
        config.side = side;

        Pose expected = new Pose(
            2.5 * TILE_WIDTH,
            config.alliance.sign * -0.65 * TILE_WIDTH,
            toRadians(config.alliance.sign * 16.8)
        );

        Pose actual = nav.getDepositNorthPose(0, 0);

        comparePose(expected, actual);
    }

    @Theory
    public void testGetSpike0(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);
        config.alliance = alliance;
        config.side = side;

        Pose expected = new Pose(
            2.1 * TILE_WIDTH,
            config.alliance.sign * -2.7 * TILE_WIDTH,
            toRadians(config.alliance.sign * -15)
        );

        Pose actual = nav.getSpike0();

        comparePose(expected, actual);
    }

    @Theory
    public void testGetStartPoseBranches(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);

        config.alliance = org.firstinspires.ftc.teamcode.game.Alliance.UNKNOWN;
        config.side = side;
        comparePose(new Pose(0, 0, 0, true), nav.getStartPose());

        config.alliance = null;
        config.side = side;
        comparePose(new Pose(0, 0, 0, true), nav.getStartPose());

        config.alliance = alliance;
        config.side = org.firstinspires.ftc.teamcode.game.Side.UNKNOWN;
        comparePose(new Pose(0, 0, 0, true), nav.getStartPose());

        config.alliance = alliance;
        config.side = null;
        comparePose(new Pose(0, 0, 0, true), nav.getStartPose());

        config.side = NORTH;
        comparePose(nav.getStartNorthPose(), nav.getStartPose());

        config.side = SOUTH;
        comparePose(nav.getStartSouthPose(), nav.getStartPose());
    }

    @Theory
    public void testGoalOffsetsAndDistance(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);
        config.alliance = alliance;
        config.side = side;

        config.pose = new Pose(2 * TILE_WIDTH, 0, 0);
        config.goalDistanceOffsetNorth = 5;
        config.goalAngleOffsetNorth = 3;
        assert nav.getGoalDistanceOffset() == 5;
        assert abs(nav.getGoalHeadingOffset() - toRadians(3)) < 0.001;
        assert nav.getGoalDistance() > 0;

        config.pose = new Pose(0, 0, 0);
        config.goalDistanceOffsetSouth = 7;
        config.goalAngleOffsetSouth = 4;
        assert nav.getGoalDistanceOffset() == 7;
        assert abs(nav.getGoalHeadingOffset() - toRadians(4)) < 0.001;
    }

    @Theory
    public void testChaseArtifactAndCreatePoseOverloads(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);
        config.alliance = alliance;
        config.side = side;
        config.pose = new Pose(10, 12, 0.5);
        vision.element = new Pose(20, 24, 1.0);

        Pose chasePose = nav.getChasePose(1);
        assert abs(chasePose.x - vision.element.x) < 0.1;
        assert abs(chasePose.y - (2.4 * TILE_WIDTH * -config.alliance.sign)) < 0.1;
        assert abs(chasePose.heading - toRadians(config.alliance.sign * -85)) < 0.1;
        vision.element = null;
        assert nav.getChasePose(4).x != 0;

        vision.element = new Pose(20, 24, 1.0);
        assert nav.getArtifactPose() != null;
        assert nav.getArtifactForwardRemaining() != 0;
        assert nav.getArtifactStrafeRemaining() != 0;
        assert nav.getArtifactHeadingRemaining() != 0;

        Pose pose = new Pose(1, 2, 0.3);
        assert nav.createPose(pose, NavSubsystem.Axial.FRONT) != null;
        assert nav.createPose(pose, NavSubsystem.Lateral.LEFT) != null;
        assert nav.createPose(pose, NavSubsystem.Axial.BACK, NavSubsystem.Lateral.RIGHT) != null;
        assert nav.createPose(1, 2, 0.3, 1.5, 2.5) != null;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Axial.FRONT) != null;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Axial.FRONT, 1.5) != null;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Lateral.LEFT) != null;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Lateral.LEFT, 2.5) != null;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Axial.BACK, NavSubsystem.Lateral.RIGHT, 1.5, 2.5) != null;
        assert nav.createPose(1, 2, true).hold;
        assert nav.createPose(pose, NavSubsystem.Axial.FRONT, true).hold;
        assert nav.createPose(pose, NavSubsystem.Lateral.LEFT, true).hold;
        assert nav.createPose(pose, NavSubsystem.Axial.BACK, NavSubsystem.Lateral.RIGHT, true).hold;
        assert nav.createPose(1, 2, 0.3, true).hold;
        assert nav.createPose(1, 2, 0.3, 1.5, 2.5, true).hold;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Axial.FRONT, true).hold;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Axial.FRONT, 1.5, true).hold;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Lateral.LEFT, true).hold;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Lateral.LEFT, 2.5, true).hold;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Axial.BACK, NavSubsystem.Lateral.RIGHT, true).hold;
        assert nav.createPose(1, 2, 0.3, NavSubsystem.Axial.BACK, NavSubsystem.Lateral.RIGHT, 1.5, 2.5, true).hold;
    }

    @Theory
    public void testGetParkingPoseBranches(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);
        config.alliance = alliance;
        config.side = side;

        Pose gate = nav.getParkingPose(true, NavSubsystem.Axial.CENTER, NavSubsystem.Lateral.CENTER);
        Pose nongateNorth = nav.getParkingPose(false, NORTH == side ? NavSubsystem.Axial.FRONT : NavSubsystem.Axial.BACK, NavSubsystem.Lateral.LEFT);

        assert gate != null;
        assert nongateNorth != null;
    }

    @Theory
    public void testGetDepositNorthBranches(Alliance alliance, Side side) {
        Assume.assumeTrue(alliance != null && side != null);
        config.alliance = alliance;
        config.side = side;

        config.pose = new Pose(0, 0, 0);
        TimingSubsystem.playTimer = org.mockito.Mockito.mock(com.qualcomm.robotcore.util.ElapsedTime.class);
        org.mockito.Mockito.when(TimingSubsystem.playTimer.seconds()).thenReturn(2.0);
        assert nav.getDepositNorthPose(0, 0) != null;

        config.pose = new Pose(0, 2 * TILE_WIDTH, 0);
        org.mockito.Mockito.when(TimingSubsystem.playTimer.seconds()).thenReturn(5.0);
        assert nav.getDepositNorthPose(1, 1) != null;
    }

    private static void comparePose(Pose expected, Pose actual) {
        assert abs(expected.x - actual.x) < 0.1;
        assert abs(expected.y - actual.y) < 0.1;
        assert abs(expected.heading - actual.heading) < 0.1;
        assert expected.hold == actual.hold;
    }
}
