package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Constants;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.CRServoEx;
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

    protected Servo getServo(String id) {
        return new Servo(id, getDevice(com.qualcomm.robotcore.hardware.Servo.class, id));
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

    /** @noinspection unused, SameParameterValue */
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
