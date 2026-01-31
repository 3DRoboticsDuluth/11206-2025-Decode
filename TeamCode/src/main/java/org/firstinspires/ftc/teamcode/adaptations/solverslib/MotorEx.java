package org.firstinspires.ftc.teamcode.adaptations.solverslib;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit.AMPS;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

@SuppressWarnings("unused")
public class MotorEx extends com.seattlesolvers.solverslib.hardware.motors.MotorEx {
    private static final double cachingTolerance = 0.0001;

    public String id;

    public MotorEx(@NonNull HardwareMap hMap, String id, @NonNull GoBILDA type) {
        super(hMap, id, type);
        this.id = id;
    }

    public double getRpm() {
        return this.motorEx.getVelocity() / this.type.getCPR() * 60;
    }

    public double getVelocityPercentage() {
        return super.getVelocity() / ACHIEVABLE_MAX_TICKS_PER_SECOND;
    }

    public void setVelocityPercentage(double percentage) {
        super.setVelocity(percentage * ACHIEVABLE_MAX_TICKS_PER_SECOND);
    }

    @Override
    public void set(double output) {
        if (runmode == RunMode.VelocityControl && (motor.getZeroPowerBehavior() == BRAKE || output != 0)) {
            double speed = bufferFraction * output * ACHIEVABLE_MAX_TICKS_PER_SECOND;
            double velocity = veloController.calculate(getCorrectedVelocity(), speed) + feedforward.calculate(speed, getAcceleration());
            setPower(velocity / ACHIEVABLE_MAX_TICKS_PER_SECOND);
        } else if (runmode == RunMode.PositionControl) {
            double error = positionController.calculate(encoder.getPosition());
            setPower(output * error);
        } else {
            setPower(output);
        }
    }

    private void setPower(double power) {
        if ((Math.abs(power - motorEx.getPower()) > cachingTolerance) || (power == 0 && motorEx.getPower() != 0)) {
            motorEx.setPower(power);
        }
    }

    @SuppressLint("DefaultLocale")
    public void addTelemetry(boolean enabled) {
        if (!enabled) return;
        telemetry.addData(id + " (amp)", () -> String.format("%.2f", this.motorEx.getCurrent(AMPS)));
        telemetry.addData(id + " (pow)", () -> String.format("%.2f", this.motorEx.getPower()));
        telemetry.addData(id + " (vel)", () -> String.format("%.2f", this.motorEx.getVelocity()));
        telemetry.addData(id + " (pos)", () -> String.format("%d", this.motorEx.getCurrentPosition()));
        telemetry.addData(id + " (vlp)", () -> String.format("%.2f", this.getVelocityPercentage()));
        telemetry.addData(id + " (rpm)", () -> String.format("%.0f", this.getRpm()));
    }
}
