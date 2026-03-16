package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.Constants;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.CRServoEx;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.function.Supplier;

public class HardwareSubsystemTests extends TestHarness {
    private static class TestDevice implements HardwareDevice {
        @Override public Manufacturer getManufacturer() { return Manufacturer.Other; }
        @Override public String getDeviceName() { return "test"; }
        @Override public String getConnectionInfo() { return "test"; }
        @Override public int getVersion() { return 1; }
        @Override public void resetDeviceConfigurationForOpMode() {}
        @Override public void close() {}
    }

    private static class ExposedHardwareSubsystem extends HardwareSubsystem {
        boolean unreadyPublic() { return unready(); }
        Follower getFollowerPublic() { return getFollower(); }
        ServoEx getServoPublic(String id) { return getServo(id); }
        ServoEx getServoPublic(String id, org.firstinspires.ftc.robotcore.external.Consumer<ServoEx> consumer) { return getServo(id, consumer); }
        CRServoEx getCRServoPublic(String id) { return getCRServo(id); }
        CRServoEx getCRServoPublic(String id, org.firstinspires.ftc.robotcore.external.Consumer<CRServoEx> consumer) { return getCRServo(id, consumer); }
        <T> T getDevicePublic(Class<? extends T> type, String name) { return getDevice(type, name); }
        <T> T getDevicePublic(Class<? extends T> type, String name, org.firstinspires.ftc.robotcore.external.Consumer<T> consumer) { return getDevice(type, name, consumer); }
        MotorEx getMotorPublic(String id, Motor.GoBILDA type) { return getMotor(id, type); }
        MotorEx getMotorPublic(String id, Motor.GoBILDA type, org.firstinspires.ftc.robotcore.external.Consumer<MotorEx> consumer) { return getMotor(id, type, consumer); }
    }

    @Test
    public void testUnreadyFalseAndTrue() {
        ExposedHardwareSubsystem subsystem = new ExposedHardwareSubsystem();
        assert !subsystem.unreadyPublic();

        subsystem.errors.add("disabled");
        assert subsystem.unreadyPublic();

        ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
        verify(telemetry).addData(eq("ExposedHardwareSubsystem"), captor.capture());
        assert captor.getValue().get().equals("Disabled (see logs)");
    }

    @Test
    public void testGetFollowerUsesConstantsFactory() {
        ExposedHardwareSubsystem subsystem = new ExposedHardwareSubsystem();
        Follower follower = org.mockito.Mockito.mock(Follower.class);

        try (MockedStatic<Constants> constants = mockStatic(Constants.class)) {
            constants.when(() -> Constants.createFollower(hardwareMap)).thenReturn(follower);
            assert subsystem.getFollowerPublic() == follower;
        }
    }

    @Test
    public void testGetServoCrServoDeviceAndMotorInvokeConsumers() {
        ExposedHardwareSubsystem subsystem = new ExposedHardwareSubsystem();
        boolean[] called = new boolean[4];

        try (
            MockedConstruction<ServoEx> servos = mockConstruction(ServoEx.class);
            MockedConstruction<CRServoEx> crservos = mockConstruction(CRServoEx.class);
            MockedConstruction<MotorEx> motors = mockConstruction(MotorEx.class)
        ) {
            assert subsystem.getServoPublic("servo") != null;
            assert subsystem.getServoPublic("servo2", s -> called[0] = true) != null;
            assert called[0];

            assert subsystem.getCRServoPublic("crservo") != null;
            assert subsystem.getCRServoPublic("crservo2", s -> called[1] = true) != null;
            assert called[1];

            assert subsystem.getDevicePublic(TestDevice.class, "device") != null;
            assert subsystem.getDevicePublic(TestDevice.class, "device2", d -> called[2] = true) != null;
            assert called[2];

            assert subsystem.getMotorPublic("motor", Motor.GoBILDA.RPM_1150) != null;
            assert subsystem.getMotorPublic("motor2", Motor.GoBILDA.RPM_1150, m -> called[3] = true) != null;
            assert called[3];
        }
    }

    @Test
    public void testGetDeviceFailureAddsErrorAndLogs() {
        ExposedHardwareSubsystem subsystem = new ExposedHardwareSubsystem();
        when(hardwareMap.get(any(Class.class), anyString())).thenThrow(new RuntimeException("boom"));

        try (MockedStatic<Log> log = mockStatic(Log.class)) {
            assert subsystem.getDevicePublic(TestDevice.class, "bad") == null;
            assert subsystem.errors.contains("boom");
            log.verify(() -> Log.e("ExposedHardwareSubsystem", "boom"));
        }
    }
}
