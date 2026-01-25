package org.firstinspires.ftc.teamcode.adaptations.solverslib;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class ServoEx extends com.seattlesolvers.solverslib.hardware.servos.ServoEx {
    public String id;

    /*public ServoEx(HardwareMap hwMap, String id, double min, double max) {
        super(hwMap, id, min, max);
    }*/

    /*public ServoEx(HardwareMap hwMap, String id, double range, AngleUnit angleUnit) {
        super(hwMap, id, range, angleUnit);
    }*/

    public ServoEx(HardwareMap hwMap, String id) {
        super(hwMap, id);
        this.id = id;
    }

    @SuppressLint("DefaultLocale")
    public void addTelemetry(boolean enabled) {
        if (!enabled) return;
        telemetry.addData(id + " (pos)", () -> String.format("%.2f", this.get()));
    }
}
