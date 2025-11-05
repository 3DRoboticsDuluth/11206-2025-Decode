package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;

import static java.lang.Math.abs;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.junit.Test;

public class NavSubsystemTests extends  TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        nav = new NavSubsystem();
    }

    @Test
    public void testGetClosestArtifactPose() {
        compare(
            new Pose(0, 0, 0),
            nav.getClosestArtifactPose()
        );
    }

    @Test
    public void testGetDepositNearPose() {
        compare(
            new Pose(0, 0, 0),
            nav.getDepositNearPose()
        );
    }

    @Test
    public void testGetDepositFarPose() {
        compare(
            new Pose(0, 0, 0),
            nav.getDepositFarPose()
        );
    }

    @Test
    public void testGetDepositAlignPose() {
        compare(
            new Pose(0, 0, 0),
            nav.getDepositAlignPose()
        );
    }

    @Test
    public void testGetHumanPlayerZonePose() {
        compare(
            new Pose(0, 0, 0),
            nav.getHumanPlayerZonePose()
        );
    }

    @Test
    public void testGetMidLaunchPose() {
        compare(
            new Pose(0, 0, 0),
            nav.getMidLaunchPose()
        );
    }

    private static void compare(Pose expected, Pose actual) {
        assert abs(expected.x - actual.x) < .1;
        assert abs(expected.y - actual.y) < .1;
        assert abs(expected.heading - actual.heading) < .1;
    }
}
