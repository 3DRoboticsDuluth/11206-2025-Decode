package org.firstinspires.ftc.teamcode.adaptations.solverslib;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class ServoEx extends com.seattlesolvers.solverslib.hardware.servos.ServoEx {
    public String id;

    public ServoEx(HardwareMap hwMap, String id) {
        super(hwMap, id, 0, 1);
        this.id = id;
    }

    public ServoEx(HardwareMap hwMap, String id, double min, double max) {
        super(hwMap, id, min, max);
    }

    public void scaleRange(double min, double max) {
        this.getServo().scaleRange(min, max);
    }

    @SuppressLint("DefaultLocale")
    public void addTelemetry(boolean enabled) {
        if (!enabled) return;
        telemetry.addData(id + " (pos)", () -> String.format("%.2f", this.get()));
    }
}
