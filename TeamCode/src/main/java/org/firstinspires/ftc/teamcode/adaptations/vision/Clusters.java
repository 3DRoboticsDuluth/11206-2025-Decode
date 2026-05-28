package org.firstinspires.ftc.teamcode.adaptations.vision;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Clusters {
    private static final double POSE_EPS = 1e-9;
    private static final double SCORE_EPS = 1e-6;

    private Clusters() {}

    public static Pose findBest(
        List<Pose> poses,
        Pose currentBest,
        int maxClusterSize,
        double minImprovement
    ) {
        Pose candidate = null;
        if (poses != null && !poses.isEmpty()) {
            int clusterSize = Math.max(1, maxClusterSize);
            double bestScore = Double.MAX_VALUE;

            for (Pose center : poses) {
                double score = score(poses, center, clusterSize);
                if (score < bestScore - SCORE_EPS || (approximatelyEqualScore(score, bestScore)
                    && isPreferredCenter(center, candidate))) {
                    bestScore = score;
                    candidate = center;
                }
            }
        }

        if (candidate == null) return currentBest;
        if (currentBest == null) return candidate;

        double candidateScore = score(poses, candidate, maxClusterSize);
        double currentScore = score(poses, currentBest, maxClusterSize);
        if (Double.isNaN(candidateScore)) return currentBest;
        if (Double.isNaN(currentScore)) return candidate;

        // minImprovement is a relative threshold (ratio), e.g. 0.05 => 5% better required.
        double required = Math.max(0, minImprovement) * Math.max(currentScore, SCORE_EPS);
        double actual = currentScore - candidateScore;
        boolean shouldSwitch = actual > required + SCORE_EPS;

        return shouldSwitch ? candidate : currentBest;
    }

    private static double score(List<Pose> poses, Pose center, int maxClusterSize) {
        if (poses == null || poses.isEmpty() || center == null) return Double.NaN;
        int clusterSize = Math.max(1, maxClusterSize);
        List<Pose> cluster = nearestCluster(poses, center, clusterSize);
        return computeScore(center, cluster);
    }

    private static List<Pose> nearestCluster(List<Pose> poses, Pose center, int clusterSize) {
        List<Pose> sorted = new ArrayList<>(poses);
        sorted.sort(Comparator.comparingDouble(other -> squaredDistance(center, other)));
        int limit = Math.min(clusterSize, sorted.size());
        return new ArrayList<>(sorted.subList(0, limit));
    }

    private static boolean approximatelyEqual(double a, double b) {
        return Math.abs(a - b) < POSE_EPS;
    }

    private static boolean approximatelyEqualScore(double a, double b) {
        return Math.abs(a - b) < SCORE_EPS;
    }

    private static boolean isPreferredCenter(Pose candidate, Pose current) {
        if (candidate == null) return false;
        if (current == null) return true;
        if (!approximatelyEqual(candidate.x, current.x)) return candidate.x < current.x;
        if (!approximatelyEqual(candidate.y, current.y)) return candidate.y < current.y;
        return candidate.heading < current.heading;
    }

    private static double computeScore(Pose center, List<Pose> cluster) {
        double total = 0;
        for (Pose pose : cluster)
            total += distance(center, pose);
        return total;
    }

    private static double distance(Pose a, Pose b) {
        return Math.sqrt(squaredDistance(a, b));
    }

    private static double squaredDistance(Pose a, Pose b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return dx * dx + dy * dy;
    }
}
