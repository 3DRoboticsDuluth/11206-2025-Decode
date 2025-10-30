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
    public static boolean TEL = false;
    public static PIDFCoefficients PIDF = new PIDFCoefficients(10, 3, 0, 0);
    public static double VEL = 0;

    public MotorEx motorLeft;
    public MotorEx motorRight;

    public FlywheelSubsystem() {
        motorLeft = getMotor("flywheelLeft", BARE, m -> configure(m, true));
        motorRight = getMotor("flywheelRight", BARE, m -> configure(m, false));
    }

    @Override
    public void periodic() {
        if (unready()) return;
        set(motorLeft, "flywheelLeft");
        set(motorRight, "flywheelRight");
    }

    public void start() {
        VEL = 0.5;
    }

    public void stop() {
        VEL = 0;
    }

    public void reverse() {
        VEL = -0.5;
    }

    public static final double TARGET_VELOCITY = 1500;
    public static final double VELOCITY_TOLERANCE = 50;

    private void configure(MotorEx motor, boolean inverted) {
        motor.motor.setZeroPowerBehavior(FLOAT);
        motor.motor.setDirection(inverted ? REVERSE : FORWARD);
        motor.motor.setMode(STOP_AND_RESET_ENCODER);
        motor.motor.setMode(RUN_USING_ENCODER);

    }

    private void set(MotorEx motor, String name) {
        motor.motorEx.setPIDFCoefficients(RUN_USING_ENCODER, PIDF);
        motor.motor.setPower(VEL);
        motor.addTelemetry(name, TEL);
    }

    public boolean isAtTargetVelocity() {
        double currentLeftVelocity = motorLeft.getVelocity();
        double currentRightVelocity = motorRight.getVelocity();

        return Math.abs(currentLeftVelocity - TARGET_VELOCITY) < VELOCITY_TOLERANCE &&
               Math.abs(currentRightVelocity - TARGET_VELOCITY) < VELOCITY_TOLERANCE;
    }
}
