package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.BARE;

import android.annotation.SuppressLint;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFCoefficients;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFController;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class FlywheelSubsystem extends HardwareSubsystem {
    public static PIDFCoefficients PIDF = new PIDFCoefficients(64, 0, 0, 8);
    //public static PIDFCoefficients PIDF = new PIDFCoefficients(128, 0, 0, 8);
    //public static PIDFCoefficients PIDF = new PIDFCoefficients(256, 0, 0, 16);
    public static FFCoefficients AXIAL_FF = new FFCoefficients(0, 0, 0);
    public static double FWD = 0.4;
    public static double REV = -0.5;
    public static double HOLD = -0.2;
    public static double STOP = 0;
    public static double THRESH = 0.98;
    public static double VEL = STOP;
    public static boolean TEL = true;

    public MotorEx motorLeft;
    public MotorEx motorRight;

    public static FFController controllerAxial = new FFController(AXIAL_FF);

    public FlywheelSubsystem() {
        motorLeft = getMotor("flywheelLeft", BARE, m -> configure(m, true));
        motorRight = getMotor("flywheelRight", BARE, m -> configure(m, false));
        VEL = STOP;
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;
        VEL = calculateVelocity();
        set(motorLeft);
        set(motorRight);
    }

    public void forward() {
        //VEL = FWD;
    }

    public void stop() {
        //VEL = STOP;
    }

    public void reverse() {
        //VEL = REV;
    }

    public void hold() {
        //VEL = HOLD;
    }

    public boolean isReady() {
        VEL = calculateVelocity();
        return motorLeft.getRpm() >= VEL * THRESH &&
            motorRight.getRpm() >= VEL * THRESH;
    }

    private double calculateVelocity() {
        return VEL;
    }

    private void configure(MotorEx motor, boolean inverted) {
        motor.motorEx.setZeroPowerBehavior(FLOAT);
        motor.motorEx.setDirection(inverted ? REVERSE : FORWARD);
        motor.motorEx.setMode(RUN_USING_ENCODER);
    }

    private void set(MotorEx motor) {
        motor.motorEx.setVelocityPIDFCoefficients(PIDF.p, PIDF.i, PIDF.d, PIDF.f);
        motor.motorEx.setVelocity(motor.getMaxRPM() / 60 * motor.getCPR() * VEL);
        motor.addTelemetry(TEL);
    }
}
