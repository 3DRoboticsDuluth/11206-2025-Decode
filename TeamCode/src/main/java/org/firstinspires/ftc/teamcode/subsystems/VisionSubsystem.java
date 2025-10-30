package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Pipeline.APRILTAG;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import android.annotation.SuppressLint;
import android.util.Log;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;

import java.util.List;

@Configurable
public class VisionSubsystem extends HardwareSubsystem {
    public static boolean TEL = false;
    public static boolean CAMERA_UPSIDE_DOWN = true;
    public static double CAMERA_X_INCHES = 5.905512;
    public static double CAMERA_Y_INCHES = -5.11811;
    public static double CAMERA_Z_INCHES = 7.6771654;
    public static double CAMERA_YAW_DEGREES = 15.7;
    public static double CAMERA_PITCH_DEGREES = -17.4;
    public static double POSE_OFFSET_X_INCHES = 0.1;
    public static double POSE_OFFSET_Y_INCHES = -0.5;
    public static double POSE_OFFSET_Z_DEGREES = 0;
    public static double IN_PER_M = 39.3701;
    public static int PIPELINE = 0;
    public static double ELEVATION_SCALAR = 0.820; //0.845
    public static double BEARING_X_SCALAR = 0.745;
    public static double BEARING_Y_SCALAR = 1.170;
    
    public Pose detectionPose = null;
    public int detectionCount = 0;
    public Pose elementPose = null;

    public final Limelight3A limelight;

    public final Servo servo;

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
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        switchPipeline(PIPELINE, false);

        detectionPose = null;

        double yaw = toDegrees(config.pose.heading);

        limelight.updateRobotOrientation(yaw);

        LLResult result = limelight.getLatestResult();

        servo.addTelemetry(TEL);

        if (result == null || !result.isValid()) {
            telemetry.addData("Vision", () -> "No data available");
            return;
        }

        if (result.getPipelineIndex() == APRILTAG.index) processBotPose(result);
        else processColorResults(result);
    }

    public void switchPipeline(int pipeline, boolean elementReset) {
        if (limelight == null) return;
        if (elementReset) elementPose = null;
        limelight.pipelineSwitch(PIPELINE = pipeline);
    }

    @SuppressLint("DefaultLocale")
    private void processBotPose(LLResult result) {
        Pose3D botpose = result.getBotpose_MT2();

        detectionPose = new Pose(
             botpose.getPosition().x * IN_PER_M + POSE_OFFSET_X_INCHES,
             botpose.getPosition().y * IN_PER_M + POSE_OFFSET_Y_INCHES,
             botpose.getOrientation().getYaw(AngleUnit.RADIANS) + toRadians(POSE_OFFSET_Z_DEGREES)
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
    private void processColorResults(LLResult result) {
        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();

        for (LLResultTypes.ColorResult cr : colorResults) {
            double direction = CAMERA_UPSIDE_DOWN ? -1 : 1;

            telemetry.addData(
                 "Vision (Color Result)",
                 () -> String.format(
                     "%.2f°tx, %.2f°ty",
                     direction * cr.getTargetXDegrees(),
                     direction * cr.getTargetYDegrees()
                 )
            );

            Log.i(
                this.getClass().getSimpleName(),
                String.format(
                    "Vision (Color Result) | %.2f°tx, %.2f°ty",
                    direction * cr.getTargetXDegrees(),
                    direction * cr.getTargetYDegrees()
                )
            );

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

            return;
        }
    }
}
