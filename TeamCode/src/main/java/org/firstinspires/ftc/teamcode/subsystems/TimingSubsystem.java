package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;

@Config
public class TimingSubsystem extends SubsystemBase {
    public static ElapsedTime playTimer = new ElapsedTime();
    public static ElapsedTime periodicTimer = new ElapsedTime();
    public static int periodicCount = 0;
    private boolean hasRumbled = false;

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        ++periodicCount;

        if (!hasRumbled && playTimer.seconds() >= 75) {
            gamepad1.gamepad.rumble(1.0, 1.0, 1000);
            gamepad2.gamepad.rumble(1.0, 1.0, 1000);
            hasRumbled = true;
        }

        telemetry.addData(
            "Timing",
            () -> String.format(
                "%.1fs, %.1fms, %.1fHz, %.1fKHz",
                playTimer.seconds(),
                periodicTimer.milliseconds(),
                1 / periodicTimer.seconds(),
                0.001 / periodicTimer.seconds()
            )
        );

        periodicTimer.reset();

        telemetry.update();
    }
}
