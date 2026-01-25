package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Constants;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.CRServoEx;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;

import java.util.ArrayList;
import java.util.function.Supplier;

/** @noinspection unused*/
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

    protected ServoEx getServo(String id) {
        return getServo(id, 0, 1, s -> {});
    }

    protected ServoEx getServo(String id, Consumer<ServoEx> consumer) {
        return getServo(id, 0, 1, consumer);
    }

    protected ServoEx getServo(String id, double min, double max) {
        return getServo(id, min, max, s -> {});
    }

    protected ServoEx getServo(String id, double min, double max, Consumer<ServoEx> consumer) {
        return getHardware(() -> {
            ServoEx servo = new ServoEx(hardwareMap, id);
            consumer.accept(servo);
            return servo;
        });
    }

    protected CRServoEx getCRServo(String id) {
        return getCRServo(id, s -> {});
    }

    protected CRServoEx getCRServo(String id, Consumer<CRServoEx> consumer) {
        return getHardware(() -> {
            CRServoEx servo = new CRServoEx(hardwareMap, id);
            consumer.accept(servo);
            return servo;
        });
    }

    /** @noinspection SameParameterValue*/
    protected <T> T getDevice(Class<? extends T> type, String deviceName) {
        return getDevice(type, deviceName, m -> {});
    }

    protected <T> T getDevice(Class<? extends T> type, String deviceName, Consumer<T> consumer) {
        return getHardware(() -> {
            T device = hardwareMap.get(type, deviceName);
            consumer.accept(device);
            return device;
        });
    }

    /** @noinspection SameParameterValue */
    protected MotorEx getMotor(String id, Motor.GoBILDA type) {
        return getMotor(id, type, m -> {});
    }

    protected MotorEx getMotor(String id, Motor.GoBILDA type, Consumer<MotorEx> consumer) {
        return getHardware(() -> {
            MotorEx motor = new MotorEx(hardwareMap, id, type);
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
