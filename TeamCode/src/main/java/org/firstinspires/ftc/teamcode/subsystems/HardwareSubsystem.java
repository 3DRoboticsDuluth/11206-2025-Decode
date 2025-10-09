package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.Consumer;

import java.util.ArrayList;

public class HardwareSubsystem extends SubsystemBase {
    ArrayList<String> errors = new ArrayList<>();

    @Override
    public void periodic() {
        for (String error : errors)
            telemetry.addData("Error", () -> error);

        if (errors.isEmpty())
            super.periodic();
    }

    public <T> T getDevice(Class<? extends T> classOrInterface, String deviceName) {
        try {
            return hardwareMap.get(classOrInterface, deviceName);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return null;
        }
    }

    public <T> T getDevice(Class<? extends T> classOrInterface, String deviceName, Consumer<T> consumer) {
        T device = getDevice(classOrInterface, deviceName);
        if (device != null) consumer.accept(device);
        return device;
    }

    public MotorEx getMotor(String id, Motor.GoBILDA gobildaType) {
        try {
            return new MotorEx(hardwareMap, id, gobildaType);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return null;
        }
    }

    public MotorEx getMotor(String id, Motor.GoBILDA gobildaType, Consumer<MotorEx> consumer) {
        MotorEx motor = getMotor(id, gobildaType);
        if (motor != null) consumer.accept(motor);
        return motor;
    }
}
