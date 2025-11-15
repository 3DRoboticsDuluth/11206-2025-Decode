package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.commands.Commands.movingShot;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.*;
import static java.lang.Math.*;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.math.Vector;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.adaptations.balistics.ShooterModel;

@Configurable
public class MovingShotSubsystem extends SubsystemBase {

    public static boolean ENABLED = true;
    public static boolean DETAILED = true;  // ✅ Enable detailed view

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (!ENABLED) return;

        Vector velocity = drive.follower.getVelocity();
        double vx = velocity.getXComponent();
        double vy = velocity.getYComponent();
        double speed = velocity.getMagnitude();

        double distance = config.pose.hypot(nav.getGoalPose());

        telemetry.addLine("═══════ MOVING SHOT ═══════");

        // ✅ Current mode and status
        telemetry.addData("Mode", () -> movingShot.getCurrentMode().toString());
        telemetry.addData("Deposit", () -> movingShot.CURRENT_DEPOSIT.toString());
        telemetry.addData("Shot Ready", () -> movingShot.isShotReady() ? "🟢 YES" : "🔴 NO");

        // ✅ Physics calculations
        if (movingShot.currentParams != null) {
            telemetry.addData("Distance", () -> String.format("%.1f in", distance));
            telemetry.addData("Deflector Angle", () -> String.format("%.1f°", movingShot.currentParams.deflectorAngleDeg));
            telemetry.addData("Flywheel RPM", () -> String.format("%.0f", movingShot.currentParams.flywheelRPM));
            telemetry.addData("Heading Error", () -> String.format("%+.1f°", movingShot.currentParams.headingAdjustDeg));
            telemetry.addData("Flight Time", () -> String.format("%.2fs", movingShot.currentParams.flightTime));
        }

        // ✅ Robot motion
        telemetry.addData("Speed", () -> String.format("%.1f in/s", speed));
        telemetry.addData("Velocity", () -> String.format("X:%.1f Y:%.1f", vx, vy));
        telemetry.addData("Angular Vel", () -> String.format("%.1f°/s", toDegrees(velocity.getTheta())));

        // ✅ Subsystem states
        telemetry.addData("Flywheel Ready", () ->
                flywheel.motorLeft.getVelocityPercentage() >= 0.8 ? "✓" : "✗");
        telemetry.addData("Deflector Pos", () -> String.format("%.3f", deflector.POS));
        telemetry.addData("Deflector Auto", () -> deflector.AUTO_MODE ? "AUTO" : "MANUAL");

        if (DETAILED) {
            telemetry.addLine("─────────────────────────");

            // ✅ Tuning parameters being used
            telemetry.addData("K_EFF", () -> String.format("%.3f", ShooterModel.K_EFF));
            telemetry.addData("THETA_OFFSET", () -> String.format("%+.1f°", ShooterModel.THETA_OFFSET_D));
            telemetry.addData("KV_SCALE", () -> String.format("%.3f", ShooterModel.KV_SCALE));
            telemetry.addData("HEADING_TOL", () -> String.format("%.1f°", movingShot.HEADING_TOLERANCE));

            // ✅ Shot quality breakdown
            double quality = movingShot.getShotQuality();
            telemetry.addData("Shot Quality", () -> String.format("%.0f%%", quality * 100));
        }

        telemetry.addLine("═══════════════════════════");
    }
}