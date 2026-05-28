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
}
