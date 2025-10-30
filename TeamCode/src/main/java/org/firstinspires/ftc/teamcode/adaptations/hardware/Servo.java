package org.firstinspires.ftc.teamcode.adaptations.hardware;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.ServoController;

public class Servo implements com.qualcomm.robotcore.hardware.Servo {
    private final com.qualcomm.robotcore.hardware.Servo servo;

    public String id;

    public Servo(String id, com.qualcomm.robotcore.hardware.Servo servo) {
        this.id = id;
        this.servo = servo;
    }

    @Override
    public ServoController getController() {
        return servo.getController();
    }

    @Override
    public int getPortNumber() {
        return servo.getPortNumber();
    }

    @Override
    public void setDirection(Direction direction) {
        servo.setDirection(direction);
    }

    @Override
    public Direction getDirection() {
        return servo.getDirection();
    }

    @Override
    public void setPosition(double position) {
        servo.setPosition(position);
    }

    @Override
    public double getPosition() {
        return servo.getPosition();
    }

    @Override
    public void scaleRange(double min, double max) {
        servo.scaleRange(min, max);
    }

    @Override
    public Manufacturer getManufacturer() {
        return servo.getManufacturer();
    }

    @Override
    public String getDeviceName() {
        return servo.getDeviceName();
    }

    @Override
    public String getConnectionInfo() {
        return servo.getConnectionInfo();
    }

    @Override
    public int getVersion() {
        return servo.getVersion();
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        servo.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void close() {
        servo.close();
    }

    @SuppressLint("DefaultLocale")
    public void addTelemetry(boolean enabled) {
        if (!enabled) return;
        telemetry.addData(id + " (pos)", () -> String.format("%.2f", servo.getPosition()));
    }
}
