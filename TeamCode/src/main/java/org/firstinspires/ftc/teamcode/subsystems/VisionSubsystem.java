package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.adaptations.pedropathing.PoseUtil.toPedroPose;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.GREEN;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.PURPLE;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.PURPLE_LEFT;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.PURPLE_RIGHT;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.QRCODE;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.APRILTAG;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.periodicCount;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.playTimer;
import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import android.annotation.SuppressLint;
import android.util.Log;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.Style;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Drawing;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;
import org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline;
import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configurable
public class VisionSubsystem extends HardwareSubsystem {
    public static boolean CAMERA_UPSIDE_DOWN = true;
    public static double CAMERA_X_INCHES = 3.93701;    // 0.10 meters
    public static double CAMERA_Y_INCHES = -0.3937008; // 0.01 meters
    public static double CAMERA_Z_INCHES = 16.14173;   // 0.42 meters
    public static double CAMERA_PITCH_DEGREES = -0.75;
    public static double CAMERA_YAW_DEGREES = 1.15;
    public static double ELEMENT_RADIUS = 2.5;
    public static double ELEVATION_SCALAR = 1;
    public static double BEARING_X_SCALAR = 1;
    public static double BEARING_Y_SCALAR = 1;
    public static double POS_GOAL_LOCK = 0.10;
    public static double POS_CHASE_LOCK = 0.85;
    public static double POS_MIN = 0.10;
    public static double POS_MAX = 0.85;
    public static double POS = 1;
    public static double POS_LAST = POS;
    public static double DEG_MIN = -230;
    public static double DEG_MAX = 10;
    public static double DEG = 0;
    public static double PHANTOM_RADIUS = 2 * TILE_WIDTH;
    public static double PHANTOM_ANGLE = NaN;
    public static double PHANTOM_PERIOD = 15;
    public static double INCHES_PER_METER = 39.3701;
    public static boolean TEL = false;
    public static int MOD_THRESH = 3;
    public static int MOD = 6;

    public final Limelight3A limelight;
    public final ServoEx servo;

    public Pipeline PIPELINE;
    public ElapsedTime timer = new ElapsedTime();
    public LLResult result = null;
    public Pose botpose = null;
    public Pose element = null;

    // Purple artifact poses
    public List<Pose> purpleArtifacts = new ArrayList<>();

    // Green artifact poses
    public List<Pose> greenArtifacts = new ArrayList<>();

    Map<Pipeline, Consumer<LLResult>> processors;

    public VisionSubsystem() {
        if (config.auto) POS = 1;

        limelight = getDevice(
            Limelight3A.class,
            "limelight",
            l -> {
                l.pipelineSwitch((PIPELINE = QRCODE).index);
                l.start();
            }
        );

        servo = getServo("turret", s -> s.scaleRange(POS_MIN, POS_MAX));

        processors = new HashMap<Pipeline, Consumer<LLResult>>() {{
            put(QRCODE, VisionSubsystem.this::processQrCode);
            put(APRILTAG, VisionSubsystem.this::processAprilTag);
            put(GREEN, result -> processColor(result, greenArtifacts, purpleArtifacts));
            put(PURPLE, result -> processColor(result, purpleArtifacts, greenArtifacts));
        }};
    }

    /** @noinspection DataFlowIssue*/
    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        //drawArtifact();

        if (!limelight.isConnected()) {
            telemetry.addData("Vision", () -> "Connection Issue!");
            return;
        }

        botpose = null;

        double yaw = toDegrees(config.pose.heading);
        limelight.updateRobotOrientation(yaw);

        result = limelight.getLatestResult();

        if (POS_LAST != POS) {
            timer.reset();
            POS_LAST = POS;
        }

        servo.set(POS);

        telemetry.addData("Vision (Pipeline)", () -> String.format("%s", PIPELINE));
        telemetry.addData("Vision (Timer)", () -> String.format("%.1f", timer.seconds()));
        telemetry.addData("Vision (Deg)", () -> String.format("%.1f", DEG = (DEG_MAX - DEG_MIN) * POS + DEG_MIN));

        servo.addTelemetry(TEL);

        if (result == null || !result.isValid() || timer.seconds() < 0.6) {
            telemetry.addData("Vision (Results)", () -> "None available");
            return;
        }

        processors.get(PIPELINE).accept(result);
    }

    public void drawArtifact() {
        if (!isNaN(PHANTOM_ANGLE)) {
            double angle = PHANTOM_ANGLE == 0 ?
                2 * PI * playTimer.seconds() / PHANTOM_PERIOD :
                toRadians(PHANTOM_ANGLE);

            element = new Pose(
                PHANTOM_RADIUS * cos(angle),
                PHANTOM_RADIUS * sin(angle),
                0
            );
        }

        if (element == null) return;

        Drawing.drawArtifact(
            toPedroPose(element),new Style(
                        "#FF0000", "#000000", 0.5
                )
        );
    }

    public void goalLock(boolean enabled) {
        if (!enabled || !config.teleop) return;
        switchPipeline(APRILTAG, false);
        POS = POS_GOAL_LOCK;
    }

    public void chaseLock(boolean enabled) {
        if (!enabled) return;
        switchPipeline(PURPLE, true);
        POS = POS_CHASE_LOCK;
    }

    public void switchPipeline(Pipeline pipeline, boolean elementReset) {
        if (limelight == null) return;
        if (elementReset) resetElement();
        limelight.pipelineSwitch((PIPELINE = pipeline).index);
        if (PIPELINE == GREEN || PIPELINE == PURPLE) POS = POS_CHASE_LOCK;
    }

    public void resetElement() {
        element = null;
    }

    @SuppressLint("DefaultLocale")
    private void processQrCode(LLResult result) {
        List<LLResultTypes.BarcodeResult> barcodes = result.getBarcodeResults();

        for (LLResultTypes.BarcodeResult barcode : barcodes) {
            Log.i(
                this.getClass().getSimpleName(),
                String.format(
                    "Vision (QR Code) | %s | %s",
                    barcode.getFamily(),
                    barcode.getData()
                )
            );

            config.quanomous = Quanomous.process(
                barcode.getData()
            );
        }
    }

    @SuppressLint("DefaultLocale")
    private void processAprilTag(LLResult result) {
        Pose3D botpose = result.getBotpose_MT2();

        this.botpose = new Pose(
            botpose.getPosition().x * INCHES_PER_METER,
            botpose.getPosition().y * INCHES_PER_METER,
            botpose.getOrientation().getYaw(AngleUnit.RADIANS)
        );

        telemetry.addData(
            "Vision (Botpose)",
            () -> String.format("%s", this.botpose)
        );

        Log.i(
            this.getClass().getSimpleName(),
            String.format("Vision (Botpose) | %s", this.botpose)
        );

        telemetry.addData(
            "Vision (Tx, Ty, TxNC, TyNC)",
            () -> String.format(
                "%.1fTx, %.1fTy, %.1fTxNC, %.1fTyNC",
                result.getTx(),
                result.getTy(),
                result.getTxNC(),
                result.getTyNC()
            )
        );
    }

    @SuppressLint("DefaultLocale")
    private void processColor(LLResult result, List<Pose> primaryArtifacts, List<Pose> secondaryArtifacts) {
        if (!config.started) return;

        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();

        if (!colorResults.isEmpty() && periodicCount % MOD > MOD_THRESH) {

            primaryArtifacts.clear();

            for (LLResultTypes.ColorResult cr : colorResults) {

                double direction = CAMERA_UPSIDE_DOWN ? -1 : 1;
                double crx = direction * cr.getTargetXDegrees();
                double cry = direction * cr.getTargetYDegrees();

                telemetry.addData(
                    "Vision (Color Result)",
                    () -> String.format(
                        "%.2f°tx, %.2f°ty",
                        crx, cry
                    )
                );

                Log.i(
                    this.getClass().getSimpleName(),
                    String.format(
                        "Vision (Color Result) | %.2f°tx, %.2f°ty",
                        crx, cry
                    )
                );

                Pose robotCentricPose = getElementPose(crx, cry);
                Pose fieldCentricPose = config.pose.axial(robotCentricPose.x).lateral(robotCentricPose.y);

                telemetry.addData(
                    "Vision (Element Pose)",
                    fieldCentricPose::toString
                );

                Log.i(
                    this.getClass().getSimpleName(),
                    String.format(
                        "Vision (Element Pose) | %s",
                        fieldCentricPose
                    )
                );

//                if (abs(fieldCentricPose.x) < TILE_WIDTH * 2.9 &&
//                    abs(fieldCentricPose.y) < TILE_WIDTH * 3.1 &&
//                    abs(fieldCentricPose.x) > 0.25 * TILE_WIDTH &&
//                    abs(fieldCentricPose.y) > 0.25 * TILE_WIDTH)
                primaryArtifacts.add(fieldCentricPose);
            }
        }

        Stream<Pose> primaryStream = primaryArtifacts.stream();
        Stream<Pose> secondaryStream = secondaryArtifacts.stream();
        Stream<Pose> concatStream = Stream.concat(primaryStream, secondaryStream);
        ArrayList<Pose> poses = (ArrayList<Pose>)concatStream.collect(Collectors.toList());

        for (Pose pose : poses) {
            Drawing.drawArtifact(
                toPedroPose(pose)
            );
        }

        ClusterResult clusterResult = ArtifactClusterFinder.findBestCluster(poses);

        element = clusterResult == null ? element : clusterResult.artifacts.get(0);

        drawArtifact();

        if (periodicCount % MOD == MOD - 1)
            switchPipeline(PIPELINE == PURPLE ? GREEN : PURPLE, false);

        telemetry.addData("Vision (Purple Artifacts)", () -> String.format("%d", purpleArtifacts.size()));
        telemetry.addData("Vision (Green Artifacts)", () -> String.format("%d", greenArtifacts.size()));
        telemetry.addData("Vision (Pipeline)", () -> String.format("%d", PIPELINE.index));
    }

    @SuppressLint("DefaultLocale")
    private Pose getElementPose(double targetYawAngle, double targetPitchAngle) {
        double heightDiff = CAMERA_Z_INCHES - ELEMENT_RADIUS / 2;
        double elevationAngle = toRadians(CAMERA_PITCH_DEGREES + DEG + targetPitchAngle);
        double bearingAngle = toRadians(CAMERA_YAW_DEGREES - targetYawAngle);

        telemetry.addData("Vision (Height Diff)", () -> String.format("%.1f", heightDiff));
        telemetry.addData("Vision (Elevation Angle)", () -> String.format("%.1f°", toDegrees(elevationAngle)));
        telemetry.addData("Vision (Bearing Angle)", () -> String.format("%.1f°", toDegrees(bearingAngle)));

        Log.i(this.getClass().getSimpleName(), String.format("Vision (Height Diff) | %.1f", heightDiff));
        Log.i(this.getClass().getSimpleName(), String.format("Vision (Elevation Angle) | %.1f°", elevationAngle));
        Log.i(this.getClass().getSimpleName(), String.format("Vision (Bearing Angle) | %.1f°", bearingAngle));

        double distance = abs(heightDiff / tan(elevationAngle * ELEVATION_SCALAR));
        double xOffset = CAMERA_X_INCHES + distance * cos(bearingAngle * BEARING_X_SCALAR);
        double yOffset = CAMERA_Y_INCHES + distance * sin(bearingAngle * BEARING_Y_SCALAR);
        double heading = atan2(yOffset, xOffset);

        telemetry.addData("Vision (Element Distance)", () -> String.format("%.1f", distance));
        telemetry.addData("Vision (Element X Offset)", () -> String.format("%.1f", xOffset));
        telemetry.addData("Vision (Element Y Offset)", () -> String.format("%.1f", yOffset));
        telemetry.addData("Vision (Element Heading)", () -> String.format("%.1f", toDegrees(heading)));

        Log.i(this.getClass().getSimpleName(), String.format("Vision (Element Distance) | %.1f", distance));
        Log.i(this.getClass().getSimpleName(), String.format("Vision (Element X Offset) | %.1f", xOffset));
        Log.i(this.getClass().getSimpleName(), String.format("Vision (Element Y Offset) | %.1f", yOffset));
        Log.i(this.getClass().getSimpleName(), String.format("Vision (Element Heading) | %.1f", toDegrees(heading)));

        return new Pose(xOffset, yOffset, heading);
    }

    class Point {
        double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    static class ClusterResult {
        List<Pose> artifacts;
        double score;

        public ClusterResult(List<Pose> artifacts, double score) {
            this.artifacts = artifacts;
            this.score = score;
        }
    }

    public static class ArtifactClusterFinder {

        public static ClusterResult findBestCluster(List<Pose> artifacts) {
            if (artifacts.isEmpty()) return null;

            ClusterResult best = null;
            double bestScore = Double.MAX_VALUE;

            for (Pose center : artifacts) {

                // Compute distances to all artifacts
                List<PoseDistance> distances = new ArrayList<>();
                for (Pose other : artifacts) {
                    double d = distance(center, other);
                    distances.add(new PoseDistance(other, d));
                }

                // Sort by distance
                distances.sort(Comparator.comparingDouble(pd -> pd.distance));

                // Take up to 3 closest artifacts
                List<Pose> cluster = new ArrayList<>();
                int limit = Math.min(3, distances.size());

                for (int i = 0; i < limit; i++) {
                    cluster.add(distances.get(i).pose);
                }

                // Score the cluster (pairwise distances)
                double score = computeScore(cluster);

                if (score < bestScore) {
                    bestScore = score;
                    best = new ClusterResult(cluster, score);
                }
            }

            return best;
        }

        private static double computeScore(List<Pose> cluster) {
            double total = 0;

            for (int i = 0; i < cluster.size(); i++) {
                for (int j = i + 1; j < cluster.size(); j++) {
                    total += distance(cluster.get(i), cluster.get(j));
                }
            }

            return total;
        }

        private static double distance(Pose a, Pose b) {
            double dx = a.x - b.x;
            double dy = a.y - b.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        static class PoseDistance {
            public Pose pose;
            Point point;
            double distance;

            PoseDistance(Pose pose, double distance) {
                this.pose = pose;
                this.distance = distance;
            }
        }
    }
}
