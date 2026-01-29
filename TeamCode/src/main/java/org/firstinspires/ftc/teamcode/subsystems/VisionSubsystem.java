package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.GREEN;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.PURPLE;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.QRCODE;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline.APRILTAG;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
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
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;
import org.firstinspires.ftc.teamcode.adaptations.vision.Pipeline;
import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class VisionSubsystem extends HardwareSubsystem {
    public static boolean CAMERA_UPSIDE_DOWN = true;
    public static double CAMERA_X_INCHES = -8.472874016;
    public static double CAMERA_Y_INCHES = -0.014212598;
    public static double CAMERA_Z_INCHES = 10.00492126;
    public static double CAMERA_YAW_DEGREES = 0;
    public static double ELEMENT_HEIGHT = 5;
    public static double ELEVATION_SCALAR = 1;
    public static double BEARING_X_SCALAR = 1;
    public static double BEARING_Y_SCALAR = 1;
    public static double POS_GOAL_LOCK = 0.1;
    public static double POS_MIN = .1;
    public static double POS_MAX = .85;
    public static double POS = 0.5; //POS_GOAL_LOCK;
    public static double DEG_MIN = -232.5;
    public static double DEG_MAX = 67.5;
    public static double DEG = 0;
    public static boolean TEL = false;

    public final Limelight3A limelight;
    public final ServoEx servo;

    public Pipeline PIPELINE;
    public Pose detectionPose = null;
    public int detectionCount = 0;
    public Pose elementPose = null;

    Map<Pipeline, Consumer<LLResult>> processors;

    public VisionSubsystem() {
        limelight = getDevice(
            Limelight3A.class,
            "limelight",
            l -> {
                l.pipelineSwitch((PIPELINE = QRCODE).index);
                l.start();
            }
        );

        servo = getServo("turret", POS_MIN, POS_MAX);

        processors = new HashMap<Pipeline, Consumer<LLResult>>() {{
            put(QRCODE, VisionSubsystem.this::processQrCode);
            put(APRILTAG, VisionSubsystem.this::processAprilTag);
            put(GREEN, VisionSubsystem.this::processColor);
            put(PURPLE, VisionSubsystem.this::processColor);
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

        detectionPose = null;

        double yaw = toDegrees(config.pose.heading);
        limelight.updateRobotOrientation(yaw);

        LLResult result = limelight.getLatestResult();

        servo.set(POS);

        telemetry.addData("Vision (Pipeline)", () -> String.format("%s", PIPELINE));
        telemetry.addData("Vision (Deg)", () -> String.format("%.1f", DEG = (DEG_MAX - DEG_MIN) * POS + DEG_MIN));

        servo.addTelemetry(TEL);

        if (result == null || !result.isValid()) {
            telemetry.addData("Vision (Results)", () -> "None available");
            return;
        }

        processors.get(PIPELINE).accept(result);
    }

    public void goalLock(boolean enabled) {
        if (!enabled) return;
        switchPipeline(APRILTAG, false);
        POS = POS_GOAL_LOCK;
    }

    public void switchPipeline(Pipeline pipeline, boolean elementReset) {
        if (limelight == null) return;
        if (elementReset) elementPose = null;
        limelight.pipelineSwitch((PIPELINE = pipeline).index);
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

        telemetry.addData(
            "Vision (TxNC, TyNC)",
            () -> String.format(
                "%.1fx°, %.1fy°",
                result.getTxNC(),
                result.getTyNC()
            )
        );
    }

    @SuppressLint("DefaultLocale")
    private void processColor(LLResult result) {
        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();

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

            elementPose = getElementPose(crx, cry);

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
    }

    @SuppressLint("DefaultLocale")
    private Pose getElementPose(double targetYawAngle, double targetPitchAngle) {
        double heightDiff = CAMERA_Z_INCHES - ELEMENT_HEIGHT / 2;
        double elevationAngle = toRadians(DEG - targetPitchAngle);
        double bearingAngle = toRadians(CAMERA_YAW_DEGREES + targetYawAngle);

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
}
