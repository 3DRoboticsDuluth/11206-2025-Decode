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
        config.alliance = alliance;
        config.side = side;

        Pose expected = new Pose(
            -0.5 * TILE_WIDTH,
            config.alliance.sign * -0.5 * TILE_WIDTH,
            toRadians(config.alliance.sign * 45)
        );

        Pose actual = nav.getDepositSouthPose();

        compare(expected, actual);
    }

    @Theory
    public void testGetLaunchFarPose(Alliance alliance, Side side) {
        config.alliance = alliance;
        config.side = side;

        Pose expected = new Pose(
            2.5 * TILE_WIDTH,
            config.alliance.sign * -0.5 * TILE_WIDTH,
            toRadians(config.alliance.sign * 20)
        );

        Pose actual = nav.getDepositNorthPose();

        compare(expected, actual);
    }

    @Theory
    public void testGetSpike0(Alliance alliance, Side side) {
        config.alliance = alliance;
        config.side = side;

        Pose expected = new Pose(
            1.8 * TILE_WIDTH,
            config.alliance.sign * -2.6 * TILE_WIDTH,
            toRadians(config.alliance.sign * -10)
        );

        Pose actual = nav.getSpike0();

        compare(expected, actual);
    }

    private static void compare(Pose expected, Pose actual) {
        assert abs(expected.x - actual.x) < 0.1;
        assert abs(expected.y - actual.y) < 0.1;
        assert abs(expected.heading - actual.heading) < 0.1;
    }
}
