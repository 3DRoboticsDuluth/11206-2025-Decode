package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Constants;

import java.util.ArrayList;

public class HardwareSubsystem extends SubsystemBase {
    public ArrayList<String> errors = new ArrayList<>();

    protected boolean hasErrors() {
        for (String error : errors)
            telemetry.addData("Error", () -> error);
        return !errors.isEmpty();
    }

    protected Follower getFollower() {
        try {
            return Constants.createFollower(hardwareMap);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return null;
        }
    }

    protected <T> T getDevice(Class<? extends T> classOrInterface, String deviceName) {
        return getDevice(classOrInterface, deviceName, m -> {});
    }

    protected <T> T getDevice(Class<? extends T> classOrInterface, String deviceName, Consumer<T> consumer) {
        try {
            T device = hardwareMap.get(classOrInterface, deviceName);
            consumer.accept(device);
            return device;
        } catch (Exception e) {
            errors.add(e.getMessage());
            return null;
        }
    }

    protected MotorEx getMotor(String id, Motor.GoBILDA gobildaType) {
        return getMotor(id, gobildaType, m -> {});
    }

    protected MotorEx getMotor(String id, Motor.GoBILDA gobildaType, Consumer<MotorEx> consumer) {
        try {
            MotorEx motor = new MotorEx(hardwareMap, id, gobildaType);
            consumer.accept(motor);
            return motor;
        } catch (Exception e) {
            errors.add(e.getMessage());
            return null;
        }
    }
}
