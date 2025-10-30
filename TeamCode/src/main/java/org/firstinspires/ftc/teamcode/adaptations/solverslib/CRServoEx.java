package org.firstinspires.ftc.teamcode.adaptations.solverslib;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class CRServoEx extends com.seattlesolvers.solverslib.hardware.motors.CRServoEx {
    public String id;

    public CRServoEx(HardwareMap hwMap, String id) {
        super(hwMap, id);
        this.id = id;
    }

    @SuppressLint("DefaultLocale")
    public void addTelemetry(boolean enabled) {
        if (!enabled) return;
        telemetry.addData(id + " (pow)", () -> String.format("%.2f", this.get()));
        telemetry.addData(id + " (rpm)", () -> String.format("%.0f", this.getVelocity() / this.getCPR() * 60));
        telemetry.addData(id + " (pos)", () -> String.format("%d", this.getCurrentPosition()));
    }
}
