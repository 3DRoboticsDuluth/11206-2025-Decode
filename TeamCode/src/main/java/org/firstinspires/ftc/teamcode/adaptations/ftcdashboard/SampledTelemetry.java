package org.firstinspires.ftc.teamcode.adaptations.ftcdashboard;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.function.Supplier;

@Config
public class SampledTelemetry {
    public static double SAMPLES_PER_SECOND = 4;

    private final ElapsedTime timer = new ElapsedTime();

    private final Telemetry telemetry;

    private boolean sample = false;

    public SampledTelemetry(Telemetry telemetry) {
        this(telemetry, SAMPLES_PER_SECOND);
    }

    public SampledTelemetry(Telemetry telemetry, double samplesPerSecond) {
        this.telemetry = telemetry;
        SAMPLES_PER_SECOND = samplesPerSecond;
    }

    public void addData(String caption, Supplier<String> supplier) {
        if (sample) {
            try {
                telemetry.addData(caption, supplier.get());
            } catch (Exception e) {
                // TODO: Determine why april tag metadata is crashing the bot sometimes
                Log.e("SampledTelemetry", "Error adding telemetry while evaluating `func.value()`", e);
            }
        }
    }

    public void addLine() {
        if (sample) telemetry.addLine();
    }

    public void addLine(String caption) {
        if (sample) telemetry.addLine(caption);
    }

    public void update() {
        if (sample) {
            telemetry.update();
            timer.reset();
        }

        sample = timer.seconds() > 1 / SAMPLES_PER_SECOND;
    }
}
