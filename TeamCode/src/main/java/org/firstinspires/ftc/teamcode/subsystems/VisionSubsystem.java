package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Pipeline.APRILTAG;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.hardware;
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

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.game.Pose;

import java.util.List;

@Config
public class VisionSubsystem extends SubsystemBase {
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

    public VisionSubsystem() {
        if (hardware.limelight == null) return;
        switchPipeline(PIPELINE, true);
        hardware.limelight.start();
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (hardware.limelight == null) {
            telemetry.addData("Vision", () -> "Not initialized in Hardware");
            return;
        }

        switchPipeline(PIPELINE, false);

        detectionPose = null;

        double yaw = toDegrees(config.pose.heading);

        hardware.limelight.updateRobotOrientation(yaw);

        LLResult result = hardware.limelight.getLatestResult();

        if (result == null || !result.isValid()) {
            telemetry.addData("Vision", () -> "No data available");
            return;
        }

        if (result.getPipelineIndex() == APRILTAG.index) processBotPose(result);
        else processColorResults(result);
    }

    public void switchPipeline(int pipeline, boolean elementReset) {
        if (hardware.limelight == null) return;
        if (elementReset) elementPose = null;
        hardware.limelight.pipelineSwitch(PIPELINE = pipeline);
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

            elementPose = getElementPose(
                direction * cr.getTargetXDegrees(),
                direction * cr.getTargetYDegrees()
            );

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

    @SuppressLint("DefaultLocale")
    private Pose getElementPose(double targetYawAngle, double targetPitchAngle) {
        double heightDiff = CAMERA_Z_INCHES - config.intake.element.height;
        double elevationAngle = toRadians(-CAMERA_PITCH_DEGREES - targetPitchAngle);
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
}
