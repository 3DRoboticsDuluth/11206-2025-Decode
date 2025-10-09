package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.hardware;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.opMode;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.playTimer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.adaptations.ftcdashboard.SampledTelemetry;
import org.firstinspires.ftc.teamcode.commands.Commands;
import org.firstinspires.ftc.teamcode.commands.ConfigCommands;
import org.firstinspires.ftc.teamcode.commands.DriveCommands;
import org.firstinspires.ftc.teamcode.commands.VisionCommands;
import org.firstinspires.ftc.teamcode.commands.WaitCommands;
import org.firstinspires.ftc.teamcode.game.Config;
import org.firstinspires.ftc.teamcode.game.Pose;
import org.firstinspires.ftc.teamcode.opmodes.OpMode;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.HardwareSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;
import org.junit.Before;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestHarness {
    @Before
    public void setUp() {
        hardwareMap = mock(HardwareMap.class);

        when(hardwareMap.get(any(Class.class), anyString()))
            .thenAnswer(
                invocation -> mock((Class<?>)invocation.getArgument(0))
            );

        telemetry = mock(SampledTelemetry.class);

        opMode = mock(OpMode.class);

        hardware = mock(Hardware.class);
        hardware.limelight = mock(Limelight3A.class);

        gamepad1 = new GamepadEx(mock(Gamepad.class));
        gamepad2 = new GamepadEx(mock(Gamepad.class));

        Config.config = mock(Config.class);

        Subsystems.config = mock(ConfigSubsystem.class);
        Subsystems.nav = mock(NavSubsystem.class);
        Subsystems.drive = mock(DriveSubsystem.class);
        Subsystems.vision = mock(VisionSubsystem.class);
        Subsystems.vision.elementPose = new Pose(0, 0, 0);
        Subsystems.timing = mock(TimingSubsystem.class);

        playTimer = mock(ElapsedTime.class);

        Commands.wait = mock(WaitCommands.class, invocation -> new InstantCommand());
        Commands.config = mock(ConfigCommands.class, RETURNS_DEEP_STUBS);
        Commands.drive = mock(DriveCommands.class, RETURNS_DEEP_STUBS);
        Commands.vision = mock(VisionCommands.class, RETURNS_DEEP_STUBS);
    }

    public void input(Runnable runnable) {
        runnable.run();
        CommandScheduler.getInstance().run();
    }

//    protected <T extends HardwareSubsystem> T spyHardwareSubsystem(Class<T> clazz) {
//        try {
//            T real = clazz.getDeclaredConstructor().newInstance();
//            T spy = spy(real);
//
//            doAnswer(inv -> mockDevice(inv.getArgument(0)))
//                .when(spy).getDevice(any(), anyString());
//
//            doAnswer(inv -> mockMotor())
//                .when(spy).getMotor(anyString(), any());
//
//            return spy;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    protected <T extends HardwareSubsystem> T mockHardwareSubsystem(Class<T> clazz) {
        Answer hwAnswer = inv -> {
            String name = inv.getMethod().getName();
            Object[] args = inv.getArguments();

            if (name.equals("getDevice") && args.length == 2 && args[0] instanceof Class) {
                @SuppressWarnings("unchecked")
                Class<?> type = (Class<?>) args[0];
                return mockDevice(type);
            }

            if (name.equals("getMotor") && args.length >= 2) {
                return mockMotor();
            }

            return Answers.CALLS_REAL_METHODS.answer(inv);
        };

        return Mockito.mock(
            clazz,
            withSettings()
                .useConstructor()
                .defaultAnswer(hwAnswer)
        );
    }

    protected static <T> T mockDevice(Class<T> deviceType) {
        return mock(deviceType);
    }

    protected MotorEx mockMotor() {
        MotorEx motor = mock(MotorEx.class);
        motor.motorEx = mock(DcMotorEx.class);
        return motor;
    }
}
