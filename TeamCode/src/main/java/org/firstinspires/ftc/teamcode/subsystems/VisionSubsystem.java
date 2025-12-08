package org.firstinspires.ftc.teamcode.subsystems;

import static androidx.core.math.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.COLOR;
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

    public Pose detectionPose = null;
    public int detectionCount = 0;
    public Pose elementPose = null;

    public String qrCodeData = null;
    public boolean qrScanRequested = false;

    public static double MAX = .175;
    public static double MIN = 0;
    public static double SCAN_MAX = .1;
    public static double SCAN_MIN = .05;
    public static double POS = MIN;
    public static double SCAN_STEP = 0.005;
    //TODO - Tune for camera
    public static double CAMERA_HEIGHT = 11.625; // inches above ground
    public static double CAMERA_PITCH_BASE = 15.0; // degrees down from horizontal when servo at 0
    public static double ARTIFACT_HEIGHT = 5;
    public static double SERVO_ANGLE_MIN = -15.0; // degrees when servo at MIN position
    public static double SERVO_ANGLE_MAX = 15.0; // degrees when servo at MAX position

    public boolean artifactScanActive = false;
    private int scanDirection = 1;
    public boolean artifactDetected = false;

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
            put(COLOR.index, VisionSubsystem.this::processColor);
        }};

    }

    /** @noinspection DataFlowIssue*/
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

        servo.setPosition(
            POS = clamp(POS, MIN, MAX)
        );

        servo.addTelemetry(TEL);

        if (artifactScanActive && !artifactDetected) {
            POS += SCAN_STEP * scanDirection;
            if (POS >= SCAN_MAX) {
                POS = SCAN_MAX;
                scanDirection = -1;
            } else if (POS <= SCAN_MIN) {
                POS = SCAN_MIN;
                scanDirection = 1;
            }

            // Calculate current camera pitch for telemetry
            double servoFraction = (POS - MIN) / (MAX - MIN);
            double servoAngle = SERVO_ANGLE_MIN + servoFraction * (SERVO_ANGLE_MAX - SERVO_ANGLE_MIN);
            double currentPitch = CAMERA_PITCH_BASE + servoAngle;

            telemetry.addData("Vision (Scan)", () ->
                String.format("Scanning: %.3f (pitch: %.1f°)", POS, currentPitch));
        }

        if (result == null || !result.isValid()) {
            telemetry.addData("Vision", () -> "No data available");
            return;
        }

        processors.get(PIPELINE).accept(result);
    }

    public void switchPipeline(int pipeline, boolean elementReset) {
        if (limelight == null) return;
        if (elementReset) {
            elementPose = null;
            artifactDetected = false;
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

            calculateArtifactPose(tx, ty);

            // Stop scanning when artifact is detected
            if (artifactScanActive && elementPose != null) {
                artifactDetected = true;
                artifactScanActive = false;

                telemetry.addData("Vision", () -> "Artifact Detected!");
                Log.i(this.getClass().getSimpleName(), "Artifact detected, stopping scan");
            }

            if (elementPose != null) {
                telemetry.addData(
                    "Vision (Element Pose)",
                    () -> String.format(
                        "%.1fx, %.1fy, %.1f°",
                        elementPose.x,
                        elementPose.y,
                        toDegrees(elementPose.heading)
                    )
                );

                Log.i(
                    this.getClass().getSimpleName(),
                    String.format(
                        "Vision (Element Pose) | %.1fx, %.1fy, %.1f°",
                        elementPose.x,
                        elementPose.y,
                        toDegrees(elementPose.heading)
                    )
                );
            }

            return; // Only process first detection
        }
    }

    private void calculateArtifactPose(double tx, double ty) {
        double txRad = toRadians(tx);
        double tyRad = toRadians(ty);

        // Calculate current camera pitch based on servo position
        // Linear interpolation between min and max servo angles
        double servoFraction = (POS - MIN) / (MAX - MIN);
        double servoAngle = SERVO_ANGLE_MIN + servoFraction * (SERVO_ANGLE_MAX - SERVO_ANGLE_MIN);
        double currentCameraPitch = CAMERA_PITCH_BASE + servoAngle;
        double cameraPitchRad = toRadians(currentCameraPitch);

        // Calculate distance to artifact using simple trigonometry
        // Distance = (camera_height - artifact_height) / tan(camera_pitch + ty)
        double heightDiff = CAMERA_HEIGHT - ARTIFACT_HEIGHT;
        double angleToArtifact = cameraPitchRad + tyRad;

        // Prevent division by zero or negative tangent
        if (angleToArtifact <= 0 || angleToArtifact >= toRadians(85)) {
            Log.w(this.getClass().getSimpleName(),
                "Invalid angle to artifact: " + toDegrees(angleToArtifact));
            return;
        }

        double distance = heightDiff / Math.tan(angleToArtifact);

        // Clamp distance to reasonable values
        distance = clamp(distance, 6.0, 60.0);

        // Calculate horizontal angle in robot frame
        // The servo angle already accounts for vertical scanning,
        // tx is the horizontal offset
        double horizontalAngle = txRad;

        // Total horizontal angle = robot heading + horizontal camera angle
        double totalAngle = config.pose.heading + horizontalAngle;

        // Calculate artifact position in field coordinates
        double artifactX = config.pose.x + distance * cos(totalAngle);
        double artifactY = config.pose.y + distance * sin(totalAngle);

        // Calculate heading to face the artifact
        double headingToArtifact = atan2(
            artifactY - config.pose.y,
            artifactX - config.pose.x
        );

        elementPose = new Pose(artifactX, artifactY, headingToArtifact);

        Log.i(this.getClass().getSimpleName(),
            String.format("Calculated artifact: dist=%.1f, servo=%.3f (%.1f°), pitch=%.1f°",
                distance, POS, toDegrees(servoAngle), currentCameraPitch));
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
        switchPipeline(COLOR.index, true);
        artifactScanActive = true;
        POS = SCAN_MIN;
        scanDirection = 1;
        servo.setPosition(POS);
    }

    public void stopArtifactScan() {
        artifactScanActive = false;
        artifactDetected = false;
        switchPipeline(PIPELINE, false);
    }

    public boolean hasArtifactDetected() {
        return artifactDetected && elementPose != null;
    }
}