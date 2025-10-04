package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.BARE;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.hardware;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.config.Config;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@Config
public class DepositSubsystem extends SubsystemBase {
    public static double DEPOSIT_LEFT_VELOCITY = 0.5;
    public static double DEPOSIT_RIGHT_VELOCITY = 0.5;
    public static double LEFT_WHEEL_POWER;
    public static double RIGHT_WHEEL_POWER;

    public static double KS = 0;
    public static double KA = 0;
    public static double KV = 0;

    public static double WHEEL_SPEED;


    public DepositSubsystem() {
        hardware.depositLeft = new MotorEx(hardwareMap, "depositLeft", BARE);
        hardware.depositRight = new MotorEx(hardwareMap, "depositRight", BARE);

        hardware.depositLeft.setRunMode(VelocityControl);
        hardware.depositRight.setRunMode(VelocityControl);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        hardware.depositLeft.setFeedforwardCoefficients(KS, KA, KV);
        hardware.depositRight.setFeedforwardCoefficients(KS, KA, KV);

        hardware.depositLeft.setVelocity(LEFT_WHEEL_POWER);
        hardware.depositRight.setVelocity(RIGHT_WHEEL_POWER);

        hardware.depositLeft.setRunMode(VelocityControl);
        hardware.depositRight.setRunMode(VelocityControl);
    }

    public void launch() {
        LEFT_WHEEL_POWER = DEPOSIT_LEFT_VELOCITY;
        RIGHT_WHEEL_POWER= DEPOSIT_RIGHT_VELOCITY;
    }

    public void stop() {
        LEFT_WHEEL_POWER = 0;
        RIGHT_WHEEL_POWER = 0;
    }
}
