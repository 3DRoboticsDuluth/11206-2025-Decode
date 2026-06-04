package org.firstinspires.ftc.teamcode.adaptations.vision;

import static org.junit.Assert.assertSame;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ClustersTests {
    @Test
    public void testAllPosesClusterStillChoosesBestCenter() {
        Pose left = new Pose(0, 0, 0);
        Pose middle = new Pose(10, 0, 0);
        Pose right = new Pose(25, 0, 0);
        List<Pose> poses = Arrays.asList(left, middle, right);

        Pose best = Clusters.findBest(poses, null, 3, 0);

        assertSame(middle, best);
    }

    @Test
    public void testEqualScoreDoesNotSwitchAtZeroImprovement() {
        Pose left = new Pose(0, 0, 0);
        Pose middle = new Pose(10, 0, 0);
        Pose right = new Pose(20, 0, 0);
        Pose currentBest = new Pose(10, 0, 0);
        List<Pose> poses = Arrays.asList(left, middle, right);

        Pose best = Clusters.findBest(poses, currentBest, 3, 0);

        assertSame(currentBest, best);
    }

    @Test
    public void testTurnWeightPrefersPoseRequiringLessTurn() {
        Pose behind = new Pose(-20, 0, 0);
        Pose ahead = new Pose(20, 0, 0);
        Pose robotPose = new Pose(0, 0, 0);
        List<Pose> poses = Arrays.asList(behind, ahead);

        Pose best = Clusters.findBest(robotPose, poses, null, 1, 0, 0, 8, 14);

        assertSame(ahead, best);
    }

    @Test
    public void testFindClosestChoosesNearestPoseToRobot() {
        Pose near = new Pose(10, 0, 0);
        Pose far = new Pose(30, 0, 0);
        Pose robotPose = new Pose(0, 0, 0);
        List<Pose> poses = Arrays.asList(far, near);

        Pose best = Clusters.findClosest(robotPose, poses, null);

        assertSame(near, best);
    }

    @Test
    public void testFindClosestKeepsCurrentBestWithoutRobotPose() {
        Pose currentBest = new Pose(10, 0, 0);
        Pose other = new Pose(0, 0, 0);
        List<Pose> poses = Arrays.asList(other);

        Pose best = Clusters.findClosest(null, poses, currentBest);

        assertSame(currentBest, best);
    }
}
