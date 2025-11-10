package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.BARE;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class FlywheelSubsystem extends HardwareSubsystem {
    public static PIDFCoefficients PIDF = new PIDFCoefficients(150, 3, 0, 0);
    public static double HOLD = -0.2;
    public static double FWD = 0.75;
    public static double REV = -0.5;
    public static double STOP = 0;
    public static double THRESH = 0.8;
    public static double VEL = STOP;
    public static boolean TEL = false;

    public MotorEx motorLeft;
    public MotorEx motorRight;

    public FlywheelSubsystem() {
        motorLeft = getMotor("flywheelLeft", BARE, m -> configure(m, true));
        motorRight = getMotor("flywheelRight", BARE, m -> configure(m, false));
        VEL = STOP;
    }

    @Override
    public void periodic() {
        if (unready()) return;
        set(motorLeft);
        set(motorRight);
    }

    public void forward() {
        VEL = FWD;
    }

    public void stop() {
        VEL = STOP;
    }

    public void reverse() {
        VEL = REV;
    }

    public void hold(){VEL = HOLD;}

    public boolean isReady() {
        return motorLeft.getVelocityPercentage() >= VEL * THRESH &&
            motorRight.getVelocityPercentage() >= VEL * THRESH;
    }

    private void configure(MotorEx motor, boolean inverted) {
        motor.motor.setZeroPowerBehavior(FLOAT);
        motor.motor.setDirection(inverted ? REVERSE : FORWARD);
        motor.motor.setMode(STOP_AND_RESET_ENCODER);
        motor.motor.setMode(RUN_USING_ENCODER);
    }

    private void set(MotorEx motor) {
        motor.motorEx.setPIDFCoefficients(RUN_USING_ENCODER, PIDF);
        motor.motor.setPower(VEL);
        motor.addTelemetry(TEL);
    }
}
