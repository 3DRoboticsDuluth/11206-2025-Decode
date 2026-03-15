package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Alliance.BLUE;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;

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

    private static void comparePose(Pose expected, Pose actual) {
        assert abs(expected.x - actual.x) < 0.1;
        assert abs(expected.y - actual.y) < 0.1;
        assert abs(expected.heading - actual.heading) < 0.1;
    }
}
