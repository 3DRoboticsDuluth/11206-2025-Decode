package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Constants;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

import java.util.ArrayList;
import java.util.function.Supplier;

public class HardwareSubsystem extends SubsystemBase {
    ArrayList<String> errors = new ArrayList<>();

    protected boolean unready() {
        if (!errors.isEmpty())
            telemetry.addData(this.getClass().getSimpleName(), () -> "Disabled (see logs)");
        return !errors.isEmpty();
    }

    protected Follower getFollower() {
        return getHardware(
            () -> Constants.createFollower(hardwareMap)
        );
    }

    /** @noinspection SameParameterValue*/
    protected <T> T getDevice(Class<? extends T> classOrInterface, String deviceName) {
        return getDevice(classOrInterface, deviceName, m -> {});
    }

    protected <T> T getDevice(Class<? extends T> classOrInterface, String deviceName, Consumer<T> consumer) {
        return getHardware(() -> {
            T device = hardwareMap.get(classOrInterface, deviceName);
            consumer.accept(device);
            return device;
        });
    }

    /** @noinspection unused, SameParameterValue */
    protected MotorEx getMotor(String id, Motor.GoBILDA gobildaType) {
        return getMotor(id, gobildaType, m -> {});
    }

    protected MotorEx getMotor(String id, Motor.GoBILDA gobildaType, Consumer<MotorEx> consumer) {
        return getHardware(() -> {
            MotorEx motor = new MotorEx(hardwareMap, id, gobildaType);
            consumer.accept(motor);
            return motor;
        });
    }

    private <T> T getHardware(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            //noinspection DataFlowIssue
            Log.e(this.getClass().getSimpleName(), e.getMessage());
            errors.add(e.getMessage());
            return null;
        }
    }
}
