package org.firstinspires.ftc.teamcode.opmodes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Objects;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.telemetry.SampledTelemetry;
import org.firstinspires.ftc.teamcode.commands.AutoCommands;
import org.firstinspires.ftc.teamcode.commands.ConfigCommands;
import org.firstinspires.ftc.teamcode.commands.ConveyorCommands;
import org.firstinspires.ftc.teamcode.commands.DeflectorCommands;
import org.firstinspires.ftc.teamcode.commands.DriveCommands;
import org.firstinspires.ftc.teamcode.commands.FlywheelCommands;
import org.firstinspires.ftc.teamcode.commands.GateCommands;
import org.firstinspires.ftc.teamcode.commands.IntakeCommands;
import org.firstinspires.ftc.teamcode.commands.KickstandCommands;
import org.firstinspires.ftc.teamcode.commands.LightsCommands;
import org.firstinspires.ftc.teamcode.commands.QuanomousCommands;
import org.firstinspires.ftc.teamcode.commands.VisionCommands;
import org.firstinspires.ftc.teamcode.commands.WaitCommands;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.KickstandSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LightsSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

abstract class OpModeTestSupport extends TestHarness {
    protected static void prepare(OpMode opMode) {
        //noinspection UnnecessaryLocalVariable
        com.qualcomm.robotcore.eventloop.opmode.OpMode coreOpMode = opMode;
        coreOpMode.telemetry = mock(Telemetry.class);
        coreOpMode.hardwareMap = mock(HardwareMap.class);
        coreOpMode.gamepad1 = mock(Gamepad.class);
        coreOpMode.gamepad2 = mock(Gamepad.class);
    }

    protected static void setLinearOpModeState(OpMode opMode, String fieldName, boolean value) {
        try {
            Field field = Objects.requireNonNull(
                com.qualcomm.robotcore.eventloop.opmode.OpMode.class.getSuperclass()
            ).getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(opMode, value);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected static class TestWaitOpMode extends OpMode {}

    protected static class TestTeleOpMode extends TeleOpMode {
        protected int waitForStartCalls;

        protected TestTeleOpMode() {
            prepare(this);
        }

        @Override
        public void waitForStart() {
            waitForStartCalls++;
        }
    }

    protected static class TestAutoOpMode extends AutoOpMode {
        protected int waitForStartCalls;
        protected Runnable onWait;
        protected Command scheduledCommand;

        protected TestAutoOpMode() {
            prepare(this);
        }

        @Override
        public void waitForStart() {
            waitForStartCalls++;
            if (onWait != null) onWait.run();
        }

        @Override
        public void schedule(Command... commands) {
            if (commands.length > 0) scheduledCommand = commands[0];
        }
    }

    protected static class OpModeDependencies implements AutoCloseable {
        protected final MockedStatic<CommandScheduler> schedulerMock;
        protected final MockedConstruction<AutoCommands> autoCommandsMock;
        private final MockedConstruction<SampledTelemetry> sampledTelemetryMock;
        private final MockedConstruction<ConfigSubsystem> configSubsystemMock;
        private final MockedConstruction<NavSubsystem> navSubsystemMock;
        private final MockedConstruction<DriveSubsystem> driveSubsystemMock;
        private final MockedConstruction<IntakeSubsystem> intakeSubsystemMock;
        private final MockedConstruction<ConveyorSubsystem> conveyorSubsystemMock;
        private final MockedConstruction<GateSubsystem> gateSubsystemMock;
        private final MockedConstruction<DeflectorSubsystem> deflectorSubsystemMock;
        private final MockedConstruction<FlywheelSubsystem> flywheelSubsystemMock;
        private final MockedConstruction<VisionSubsystem> visionSubsystemMock;
        private final MockedConstruction<KickstandSubsystem> kickstandSubsystemMock;
        private final MockedConstruction<LightsSubsystem> lightsSubsystemMock;
        private final MockedConstruction<TimingSubsystem> timingSubsystemMock;
        private final MockedConstruction<WaitCommands> waitCommandsMock;
        private final MockedConstruction<ConfigCommands> configCommandsMock;
        private final MockedConstruction<DriveCommands> driveCommandsMock;
        private final MockedConstruction<IntakeCommands> intakeCommandsMock;
        private final MockedConstruction<ConveyorCommands> conveyorCommandsMock;
        private final MockedConstruction<GateCommands> gateCommandsMock;
        private final MockedConstruction<DeflectorCommands> deflectorCommandsMock;
        private final MockedConstruction<FlywheelCommands> flywheelCommandsMock;
        private final MockedConstruction<VisionCommands> visionCommandsMock;
        private final MockedConstruction<LightsCommands> lightsCommandsMock;
        private final MockedConstruction<KickstandCommands> kickstandCommandsMock;
        private final MockedConstruction<QuanomousCommands> quanomousCommandsMock;

        protected OpModeDependencies() {
            this(null);
        }

        protected OpModeDependencies(Command autoCommand) {
            schedulerMock = mockStatic(CommandScheduler.class);
            schedulerMock.when(CommandScheduler::getInstance).thenReturn(mock(CommandScheduler.class));

            sampledTelemetryMock = mockConstruction(SampledTelemetry.class);
            configSubsystemMock = mockConstruction(ConfigSubsystem.class);
            navSubsystemMock = mockConstruction(NavSubsystem.class);
            driveSubsystemMock = mockConstruction(DriveSubsystem.class);
            intakeSubsystemMock = mockConstruction(IntakeSubsystem.class);
            conveyorSubsystemMock = mockConstruction(ConveyorSubsystem.class);
            gateSubsystemMock = mockConstruction(GateSubsystem.class);
            deflectorSubsystemMock = mockConstruction(DeflectorSubsystem.class);
            flywheelSubsystemMock = mockConstruction(FlywheelSubsystem.class);
            visionSubsystemMock = mockConstruction(VisionSubsystem.class);
            kickstandSubsystemMock = mockConstruction(KickstandSubsystem.class);
            lightsSubsystemMock = mockConstruction(LightsSubsystem.class);
            timingSubsystemMock = mockConstruction(TimingSubsystem.class);
            waitCommandsMock = mockConstruction(WaitCommands.class);
            configCommandsMock = mockConstruction(ConfigCommands.class);
            driveCommandsMock = mockConstruction(DriveCommands.class);
            intakeCommandsMock = mockConstruction(IntakeCommands.class);
            conveyorCommandsMock = mockConstruction(ConveyorCommands.class);
            gateCommandsMock = mockConstruction(GateCommands.class);
            deflectorCommandsMock = mockConstruction(DeflectorCommands.class);
            flywheelCommandsMock = mockConstruction(FlywheelCommands.class);
            visionCommandsMock = mockConstruction(VisionCommands.class);
            lightsCommandsMock = mockConstruction(LightsCommands.class);
            kickstandCommandsMock = mockConstruction(KickstandCommands.class);
            quanomousCommandsMock = mockConstruction(QuanomousCommands.class);
            autoCommandsMock = mockConstruction(AutoCommands.class, (mock, context) -> {
                if (autoCommand != null) when(mock.execute()).thenReturn(autoCommand);
            });
        }

        @Override
        public void close() {
            autoCommandsMock.close();
            quanomousCommandsMock.close();
            kickstandCommandsMock.close();
            lightsCommandsMock.close();
            visionCommandsMock.close();
            flywheelCommandsMock.close();
            deflectorCommandsMock.close();
            gateCommandsMock.close();
            conveyorCommandsMock.close();
            intakeCommandsMock.close();
            driveCommandsMock.close();
            configCommandsMock.close();
            waitCommandsMock.close();
            timingSubsystemMock.close();
            lightsSubsystemMock.close();
            kickstandSubsystemMock.close();
            visionSubsystemMock.close();
            flywheelSubsystemMock.close();
            deflectorSubsystemMock.close();
            gateSubsystemMock.close();
            conveyorSubsystemMock.close();
            intakeSubsystemMock.close();
            driveSubsystemMock.close();
            navSubsystemMock.close();
            configSubsystemMock.close();
            sampledTelemetryMock.close();
            schedulerMock.close();
        }
    }
}
