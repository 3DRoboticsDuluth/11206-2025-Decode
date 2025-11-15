package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.commands.Commands.movingShot;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static java.lang.Math.*;

import android.annotation.SuppressLint;

import com.pedropathing.math.Vector;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class MovingShotSubsystem extends SubsystemBase {
    
    public static boolean ENABLED = true;
    public static boolean DETAILED = false;
    private long lastRumbleTime = 0;

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (!ENABLED) return;
        
        // Get current state
        boolean ready = movingShot.isShotReady();
        double quality = movingShot.getShotQuality();
        String mode = movingShot.getCurrentMode().toString();
        
        Vector velocity = drive.follower.getVelocity();
        double vx = Subsystems.drive.follower.getVelocity().getXComponent();
        double vy = Subsystems.drive.follower.getVelocity().getYComponent();
        double speed = velocity.getMagnitude();
        
        double distance = config.pose.hypot(Subsystems.nav.getGoalPose());
        
        telemetry.addLine("───────── MOVING SHOT ─────────");
        
        telemetry.addData(
            "Status",
            () -> String.format(
                "%s | %s | %.0f%%",
                ready ? "READY" : "TRACKING",
                mode,
                quality * 100
            )
        );
        
        telemetry.addData(
            "Motion",
            () -> String.format(
                "%.1f in/s @ %.0f° | %.1f in to goal",
                speed,
                toDegrees(atan2(vy, vx)),
                distance
            )
        );
        
        // Visual quality bar
        telemetry.addData(
            "Quality",
            () -> getQualityBar(quality)
        );
        
        if (DETAILED) {
            // for tuning
            telemetry.addData(
                "Velocity",
                () -> String.format(
                    "X: %.1f, Y: %.1f",
                    vx, vy
                )
            );
            
            telemetry.addData(
                "Systems",
                () -> String.format(
                    "FW: %s | Def: %.2f | Gate: %.2f",
                    Subsystems.flywheel.isReady() ? "✓" : "✗",
                    Subsystems.deflector.POS,
                    Subsystems.gate.POS
                )
            );
            
            if (movingShot.currentParams != null) {
                telemetry.addData(
                    "Shot Params",
                    () -> String.format(
                        "Angle: %.1f° | RPM: %.0f | Heading: %+.1f°",
                        movingShot.currentParams.deflectorAngleDeg,
                        movingShot.currentParams.flywheelRPM,
                        movingShot.currentParams.headingAdjustDeg
                    )
                );
                
                telemetry.addData(
                    "Flight",
                    () -> String.format(
                        "Time: %.2fs",
                        movingShot.currentParams.flightTime
                    )
                );
            }
        }
        
        telemetry.addLine("───────────────────────────────");
    }
    
    private String getQualityBar(double quality) {
        int bars = (int)(quality * 10);
        StringBuilder sb = new StringBuilder("[");
        
        for (int i = 0; i < 10; i++) {
            if (i < bars) {
                if (quality > 0.8) sb.append("█");      // Green equivalent
                else if (quality > 0.5) sb.append("▓"); // Yellow equivalent  
                else sb.append("▒");                     // Red equivalent
            } else {
                sb.append("░");
            }
        }
        
        sb.append(String.format("] %.0f%%", quality * 100));
        return sb.toString();
    }

    public void updateRumble() {
        long now = System.currentTimeMillis();
        
        if (movingShot.isShotReady()) {
            // Ready: Short pulse every 0.5s
            if (now - lastRumbleTime > 500) {
                org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad
                    .rumble(0.5, 0.5, 100);
                lastRumbleTime = now;
            }
        } else {
            double quality = movingShot.getShotQuality();
            
            if (quality > 0.7 && now - lastRumbleTime > 1000) {
                // Close: Very light pulse
                org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1.gamepad
                    .rumble(0.2, 0.2, 50);
                lastRumbleTime = now;
            }
        }
    }
}