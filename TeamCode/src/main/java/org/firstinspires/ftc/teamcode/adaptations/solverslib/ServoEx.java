package org.firstinspires.ftc.teamcode.adaptations.solverslib;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class ServoEx extends com.seattlesolvers.solverslib.hardware.servos.ServoEx {
    private boolean reversed;

    public String id;

    public ServoEx(HardwareMap hwMap, String id) {
        super(hwMap, id);
        this.id = id;
    }

    public void scaleRange(double min, double max) {
        this.scaleRange(min, max, false);
    }

    public void scaleRange(double min, double max, boolean reversed) {
        this.getServo().scaleRange(min, max);
        this.reversed = reversed;
    }

    @Override
    public void set(double output) {
        this.getServo().setPosition(
            this.reversed ? 1 - output : output
        );
    }

    @SuppressLint("DefaultLocale")
    public void addTelemetry(boolean enabled) {
        if (!enabled) return;
        telemetry.addData(id + " (pos)", () -> String.format("%.2f", this.get()));
        telemetry.addData(id + " (rev)", () -> String.format("%b", reversed));
    }
}
