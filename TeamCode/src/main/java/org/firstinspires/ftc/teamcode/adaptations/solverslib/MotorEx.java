package org.firstinspires.ftc.teamcode.adaptations.solverslib;

import static org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit.AMPS;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

@SuppressWarnings("unused")
public class MotorEx extends com.seattlesolvers.solverslib.hardware.motors.MotorEx {
    public MotorEx(@NonNull HardwareMap hMap, String id) {
        super(hMap, id);
    }

    public MotorEx(@NonNull HardwareMap hMap, String id, @NonNull GoBILDA gobildaType) {
        super(hMap, id, gobildaType);
    }

    public MotorEx(@NonNull HardwareMap hMap, String id, double cpr, double rpm) {
        super(hMap, id, cpr, rpm);
    }

    public void setVelocityPercentage(double percentage) {
        super.setVelocity(percentage * ACHIEVABLE_MAX_TICKS_PER_SECOND);
    }

    /** @noinspection NullableProblems*/
    @SuppressLint("DefaultLocale")
    public String toString() {
        return String.format(
            "%.2f amps, %.2f pow, %.1f dis, %.1f vel",
            this.motorEx.getCurrent(AMPS),
            this.motor.getPower(),
            this.encoder.getDistance(),
            this.encoder.getRawVelocity()
        );
    }

    @SuppressLint("DefaultLocale")
    public void addTelemetry(String name, boolean enabled) {
        if (!enabled) return;
        telemetry.addData(name + " (amp)", () -> String.format("%.2f", this.motorEx.getCurrent(AMPS)));
        telemetry.addData(name + " (pow)", () -> String.format("%.2f", this.motorEx.getPower()));
        telemetry.addData(name + " (vel)", () -> String.format("%.0f", this.motorEx.getVelocity()));
        telemetry.addData(name + " (pos)", () -> String.format("%d", this.motorEx.getCurrentPosition()));
    }
}
