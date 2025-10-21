package org.firstinspires.ftc.teamcode.adaptations.telemetry;

import android.util.Log;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.function.Supplier;

@Configurable
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
        attempt(() -> telemetry.addData(caption, supplier.get()));
    }

    public void addLine() {
        attempt(telemetry::addLine);
    }

    public void addLine(String caption) {
        attempt(() -> telemetry.addLine(caption));
    }

    public void update() {
        attempt(() -> {
            telemetry.update();
            timer.reset();
        });

        sample = timer.seconds() > 1 / SAMPLES_PER_SECOND;
    }

    public void attempt(Runnable runnable) {
        try {
            if (sample) runnable.run();
        } catch (Exception e) {
            Log.e("SampledTelemetry", "Error invoking telemetry`", e);
        }
    }
}
