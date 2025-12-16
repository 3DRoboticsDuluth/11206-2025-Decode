package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.PURPLE;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.QRCODE;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.APRILTAG;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.annotation.SuppressLint;
import android.util.Log;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class VisionSubsystem extends HardwareSubsystem {
    public static boolean TEL = false;
    public static boolean CAMERA_UPSIDE_DOWN = true;
    public static int PIPELINE = 0;

    // Multi-frame detection settings
    public static int REQUIRED_CONSISTENT_FRAMES = 3;
    public static double POSITION_CONSISTENCY_THRESHOLD = 6.0; // inches
    public static double MIN_DETECTION_DISTANCE = 6.0; // inches
    public static double MAX_DETECTION_DISTANCE = 72.0; // inches

    public static boolean ENABLE_MOTION_PREDICTION = true;
    public static double PREDICTION_TIME = 0.5; // seconds ahead to predict
    public static double MIN_VELOCITY_THRESHOLD = 3.0; // inches/sec minimum to predict
    public static int VELOCITY_SAMPLE_SIZE = 3; // frames to average velocity over

    public Pose detectionPose = null;
    public int detectionCount = 0;
    public Pose elementPose = null;

    public String qrCodeData = null;
    public boolean qrScanRequested = false;

    public static double MAX = .175;
    public static double MIN = 0;
    public static double SCAN_MAX = .144;  // Start at horizontal (0°)
    public static double SCAN_MIN = .094;  // End at straight down (90°)
    public static double POS = 0;
    public static double SCAN_STEP = 0.002;

    public static double CAMERA_HEIGHT = 11.625; // inches above ground
    public static double SERVO_HORIZONTAL_POSITION = 0.144;
    public static double ARTIFACT_HEIGHT = 5.0; // estimated artifact height in inches

    public static double SERVO_DEGREES_PER_UNIT = 1580.25; // 128° / 0.081 units

    public boolean artifactScanActive = false;
    private int scanDirection = 1;
    public boolean artifactDetected = false;

    // Multi-frame tracking
    private int consecutiveDetections = 0;
    private Pose lastRawDetection = null;
    private long lastDetectionTime = 0;
    private Pose lastRobotPose = null;

    // Motion prediction tracking
    private static class PositionSample {
        Pose position;
        long timestamp;

        PositionSample(Pose position, long timestamp) {
            this.position = position;
            this.timestamp = timestamp;
        }
    }

    private java.util.ArrayList<PositionSample> positionHistory = new java.util.ArrayList<>();
    private double artifactVelocityX = 0;
    private double artifactVelocityY = 0;
    private boolean artifactIsMoving = false;

    public final Limelight3A limelight;
    public final Servo servo;

    Map<Integer, Consumer<LLResult>> processors;

    public VisionSubsystem() {
        limelight = getDevice(
            Limelight3A.class,
            "limelight",
            l -> {
                switchPipeline(PIPELINE, true);
                l.start();
            }
        );

        servo = getServo("turret");

        processors = new HashMap<Integer, Consumer<LLResult>>() {{
            put(QRCODE.index, VisionSubsystem.this::processQrCode);
            put(APRILTAG.index, VisionSubsystem.this::processAprilTag);
            put(PURPLE.index, VisionSubsystem.this::processColor);
        }};
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        if (!limelight.isConnected()) {
            telemetry.addData("Vision", () -> "Connection Issue!");
            return;
        }

        if (qrScanRequested && qrCodeData == null) {
            switchPipeline(QRCODE.index, false);
        } else if (!qrScanRequested) {
            switchPipeline(PIPELINE, false);
        }

        detectionPose = null;

        double yaw = toDegrees(config.pose.heading);
        limelight.updateRobotOrientation(yaw);

        LLResult result = limelight.getLatestResult();

        POS = clamp(POS, MIN, MAX);
        servo.addTelemetry(TEL);

        if (artifactScanActive && !artifactDetected) {
            POS -= SCAN_STEP * scanDirection;
            if (POS <= SCAN_MIN) {
                POS = SCAN_MIN;
                scanDirection = -1;
            } else if (POS >= SCAN_MAX) {
                POS = SCAN_MAX;
                scanDirection = 1;
            }
            servo.setPosition(POS);

            double servoOffset = POS - 0.175;
            double currentPitch = -38.0 + (servoOffset * SERVO_DEGREES_PER_UNIT);

            telemetry.addData("Vision (Scan)", () ->
                String.format("Scanning: %.3f (pitch: %.1f°)", POS, currentPitch));
        }

        if (result == null || !result.isValid()) {
            telemetry.addData("Vision", () -> "No data available");
            return;
        }

        processors.get(PIPELINE).accept(result);

        // Display detection tracking info
        if (artifactScanActive) {
            telemetry.addData("Vision (Frames)", () ->
                String.format("%d/%d", consecutiveDetections, REQUIRED_CONSISTENT_FRAMES));

            if (ENABLE_MOTION_PREDICTION && artifactIsMoving) {
                telemetry.addData("Vision (Artifact Motion)", () ->
                    String.format("%.1f\"/s at %.1f°",
                        Math.hypot(artifactVelocityX, artifactVelocityY),
                        toDegrees(atan2(artifactVelocityY, artifactVelocityX))));
            }
        }
    }

    public void switchPipeline(int pipeline, boolean elementReset) {
        if (limelight == null) return;
        if (elementReset) {
            elementPose = null;
            artifactDetected = false;
            resetDetectionTracking();
        }
        limelight.pipelineSwitch(PIPELINE = pipeline);
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

        detectionPose = new Pose(
            botpose.getPosition().x,
            botpose.getPosition().y,
            botpose.getOrientation().getYaw(AngleUnit.RADIANS)
        );

        telemetry.addData("Vision (Detection Count)", () -> String.format("%d", ++detectionCount));
        Log.i(this.getClass().getSimpleName(), String.format("%d", ++detectionCount));

        telemetry.addData(
            "Vision (Detection Pose)",
            () -> String.format(
                "%.1fx, %.1fy, %.1f°",
                detectionPose.x,
                detectionPose.y,
                toDegrees(detectionPose.heading)
            )
        );

        Log.i(
            this.getClass().getSimpleName(),
            String.format(
                "Vision (Detection Pose) | %.1fx, %.1fy, %.1f°",
                detectionPose.x,
                detectionPose.y,
                toDegrees(detectionPose.heading)
            )
        );
    }

    @SuppressLint("DefaultLocale")
    private void processColor(LLResult result) {
        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();

        if (colorResults.isEmpty()) {
            // No detection this frame - reset tracking
            if (consecutiveDetections > 0) {
                consecutiveDetections--;
                telemetry.addData("Vision", () -> "Detection lost, resetting...");
            }
            return;
        }

        for (LLResultTypes.ColorResult cr : colorResults) {
            double direction = CAMERA_UPSIDE_DOWN ? -1 : 1;
            double tx = direction * cr.getTargetXDegrees();
            double ty = direction * cr.getTargetYDegrees();

            telemetry.addData(
                "Vision (Color Result)",
                () -> String.format("%.2f°tx, %.2f°ty", tx, ty)
            );

            Log.i(
                this.getClass().getSimpleName(),
                String.format("Vision (Color Result) | %.2f°tx, %.2f°ty", tx, ty)
            );

            Pose rawDetection = calculateArtifactPose(tx, ty);

            if (rawDetection == null) {
                resetDetectionTracking();
                return;
            }

            // Apply robot motion compensation
            Pose compensatedPose = compensateForRobotMotion(rawDetection);

            // Update motion tracking and predict future position
            updateMotionTracking(compensatedPose);
            Pose predictedPose = ENABLE_MOTION_PREDICTION ?
                predictFuturePosition(compensatedPose) : compensatedPose;

            // Check consistency with previous detections
            if (isConsistentDetection(predictedPose)) {
                consecutiveDetections++;
                lastRawDetection = predictedPose;

                telemetry.addData("Vision (Consistent)", () ->
                    String.format("Frame %d/%d", consecutiveDetections, REQUIRED_CONSISTENT_FRAMES));

                // Check if we have enough consistent frames
                if (consecutiveDetections >= REQUIRED_CONSISTENT_FRAMES && artifactScanActive) {
                    elementPose = predictedPose;
                    artifactDetected = true;
                    artifactScanActive = false;

                    telemetry.addData("Vision", () -> "Artifact LOCKED!");
                    Log.i(this.getClass().getSimpleName(),
                        String.format("Artifact locked after %d frames at (%.1f, %.1f)%s",
                            consecutiveDetections, elementPose.x, elementPose.y,
                            artifactIsMoving ? " [MOVING]" : ""));
                }
            } else {
                // Detection not consistent, restart counting
                Log.i(this.getClass().getSimpleName(),
                    String.format("Inconsistent detection, resetting (delta: %.1f\")",
                        lastRawDetection != null ? predictedPose.hypot(lastRawDetection) : 0));
                consecutiveDetections = 1;
                lastRawDetection = predictedPose;
            }

            if (predictedPose != null) {
                telemetry.addData(
                    "Vision (Element Pose)",
                    () -> String.format(
                        "%.1fx, %.1fy, %.1f°%s",
                        predictedPose.x,
                        predictedPose.y,
                        toDegrees(predictedPose.heading),
                        artifactIsMoving ? " [PRED]" : ""
                    )
                );

                Log.i(
                    this.getClass().getSimpleName(),
                    String.format(
                        "Vision (Element Pose) | %.1fx, %.1fy, %.1f°%s",
                        predictedPose.x,
                        predictedPose.y,
                        toDegrees(predictedPose.heading),
                        artifactIsMoving ? " [PREDICTED]" : ""
                    )
                );
            }

            return; // Only process first detection
        }
    }

    private Pose calculateArtifactPose(double tx, double ty) {
        double txRad = toRadians(tx);
        double tyRad = toRadians(ty);

        // Calculate current camera pitch based on servo position
        double servoOffset = POS - 0.175;
        double cameraPitchDegrees = -38.0 + (servoOffset * SERVO_DEGREES_PER_UNIT);
        double cameraPitchRad = toRadians(cameraPitchDegrees);

        // Calculate distance to artifact
        double heightDiff = CAMERA_HEIGHT - ARTIFACT_HEIGHT;
        double angleToArtifact = cameraPitchRad + tyRad;

        // Reject invalid angles
        if (angleToArtifact <= toRadians(1) || angleToArtifact >= toRadians(85)) {
            Log.w(this.getClass().getSimpleName(),
                String.format("Invalid angle to artifact: %.1f° (pitch: %.1f°, ty: %.1f°)",
                    toDegrees(angleToArtifact), cameraPitchDegrees, ty));
            return null;
        }

        double distance = heightDiff / Math.tan(angleToArtifact);

        // Reject impossible distances
        if (distance < MIN_DETECTION_DISTANCE || distance > MAX_DETECTION_DISTANCE) {
            Log.w(this.getClass().getSimpleName(),
                String.format("Distance out of range: %.1f\" (valid: %.1f-%.1f)",
                    distance, MIN_DETECTION_DISTANCE, MAX_DETECTION_DISTANCE));
            return null;
        }

        // Calculate horizontal angle offset from robot's front (camera frame)
        // tx is the horizontal offset from camera center
        double horizontalAngleOffset = txRad;

        // Calculate the absolute heading to the artifact
        // Robot's current heading + horizontal offset from camera
        double headingToArtifact = config.pose.heading + horizontalAngleOffset;

        // Calculate artifact position in field coordinates
        // Position is calculated along the line from robot front to artifact
        double artifactX = config.pose.x + distance * cos(headingToArtifact);
        double artifactY = config.pose.y + distance * sin(headingToArtifact);

        // Return pose with heading that points robot's front towards artifact
        return new Pose(artifactX, artifactY, headingToArtifact);
    }

    private Pose compensateForRobotMotion(Pose rawDetection) {
        long currentTime = System.currentTimeMillis();

        // First detection - no compensation needed
        if (lastDetectionTime == 0 || lastRobotPose == null) {
            lastDetectionTime = currentTime;
            lastRobotPose = new Pose(config.pose.x, config.pose.y, config.pose.heading);
            return rawDetection;
        }

        // Calculate robot motion since last detection
        double dt = (currentTime - lastDetectionTime) / 1000.0;
        double robotDx = config.pose.x - lastRobotPose.x;
        double robotDy = config.pose.y - lastRobotPose.y;

        // Compensate artifact position for robot motion
        // If robot moved forward 5", the artifact's field position moved back 5" relative to us
        double compensatedX = rawDetection.x - robotDx;
        double compensatedY = rawDetection.y - robotDy;

        // Update tracking
        lastDetectionTime = currentTime;
        lastRobotPose = new Pose(config.pose.x, config.pose.y, config.pose.heading);

        // Recalculate heading to face compensated position
        double headingToArtifact = atan2(
            compensatedY - config.pose.y,
            compensatedX - config.pose.x
        );

        Log.i(this.getClass().getSimpleName(),
            String.format("Motion compensation: robot moved (%.1f, %.1f), dt=%.3fs",
                robotDx, robotDy, dt));

        return new Pose(compensatedX, compensatedY, headingToArtifact);
    }

    private boolean isConsistentDetection(Pose newDetection) {
        if (lastRawDetection == null) {
            return true; // First detection is always "consistent"
        }

        // Check if new detection is within threshold of last detection
        double distance = newDetection.hypot(lastRawDetection);
        boolean consistent = distance < POSITION_CONSISTENCY_THRESHOLD;

        if (!consistent) {
            Log.i(this.getClass().getSimpleName(),
                String.format("Inconsistent: %.1f\" from last (threshold: %.1f\")",
                    distance, POSITION_CONSISTENCY_THRESHOLD));
        }

        return consistent;
    }

    private void resetDetectionTracking() {
        consecutiveDetections = 0;
        lastRawDetection = null;
        lastDetectionTime = 0;
        lastRobotPose = null;
        positionHistory.clear();
        artifactVelocityX = 0;
        artifactVelocityY = 0;
        artifactIsMoving = false;
    }

    private void updateMotionTracking(Pose currentPosition) {
        long currentTime = System.currentTimeMillis();

        // Add current position to history
        positionHistory.add(new PositionSample(currentPosition, currentTime));

        // Keep only recent samples
        while (positionHistory.size() > VELOCITY_SAMPLE_SIZE) {
            positionHistory.remove(0);
        }

        // Need at least 2 samples to calculate velocity
        if (positionHistory.size() < 2) {
            artifactIsMoving = false;
            return;
        }

        // Calculate average velocity across samples
        double totalVelX = 0;
        double totalVelY = 0;
        int validSamples = 0;

        for (int i = 1; i < positionHistory.size(); i++) {
            PositionSample prev = positionHistory.get(i - 1);
            PositionSample curr = positionHistory.get(i);

            double dt = (curr.timestamp - prev.timestamp) / 1000.0;
            if (dt > 0 && dt < 1.0) { // Sanity check on time delta
                double dx = curr.position.x - prev.position.x;
                double dy = curr.position.y - prev.position.y;

                totalVelX += dx / dt;
                totalVelY += dy / dt;
                validSamples++;
            }
        }

        if (validSamples > 0) {
            artifactVelocityX = totalVelX / validSamples;
            artifactVelocityY = totalVelY / validSamples;

            double speed = Math.hypot(artifactVelocityX, artifactVelocityY);
            artifactIsMoving = speed >= MIN_VELOCITY_THRESHOLD;

            if (artifactIsMoving) {
                Log.i(this.getClass().getSimpleName(),
                    String.format("Artifact moving: %.1f\"/s at %.1f°",
                        speed, toDegrees(atan2(artifactVelocityY, artifactVelocityX))));
            }
        }
    }

    private Pose predictFuturePosition(Pose currentPosition) {
        if (!artifactIsMoving) {
            return currentPosition;
        }

        double predictedX = currentPosition.x + artifactVelocityX * PREDICTION_TIME;
        double predictedY = currentPosition.y + artifactVelocityY * PREDICTION_TIME;

        double headingToPredicted = atan2(
            predictedY - config.pose.y,
            predictedX - config.pose.x
        );

        return new Pose(predictedX, predictedY, headingToPredicted);
    }

    public void startQrScan() {
        qrScanRequested = true;
    }

    public void stopQrScan() {
        qrScanRequested = false;
    }

    public void startArtifactScan() {
        if (limelight == null) return;
        elementPose = null;
        artifactDetected = false;
        resetDetectionTracking();
        switchPipeline(PURPLE.index, true);
        artifactScanActive = true;
        POS = SCAN_MAX;
        scanDirection = 1;
        servo.setPosition(POS);
    }

    public void stopArtifactScan() {
        artifactScanActive = false;
        artifactDetected = false;
        resetDetectionTracking();
        switchPipeline(PIPELINE, false);
    }

    public boolean hasArtifactDetected() {
        return artifactDetected && elementPose != null;
    }
}