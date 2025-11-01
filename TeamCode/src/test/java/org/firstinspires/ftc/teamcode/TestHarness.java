package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.opMode;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.playTimer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;
import org.firstinspires.ftc.teamcode.adaptations.telemetry.SampledTelemetry;
import org.firstinspires.ftc.teamcode.commands.Commands;
import org.firstinspires.ftc.teamcode.commands.ConfigCommands;
import org.firstinspires.ftc.teamcode.commands.ConveyorCommands;
import org.firstinspires.ftc.teamcode.commands.DeflectorCommands;
import org.firstinspires.ftc.teamcode.commands.DriveCommands;
import org.firstinspires.ftc.teamcode.commands.FlywheelCommands;
import org.firstinspires.ftc.teamcode.commands.GateCommands;
import org.firstinspires.ftc.teamcode.commands.IntakeCommands;
import org.firstinspires.ftc.teamcode.commands.SortingCommands;
import org.firstinspires.ftc.teamcode.commands.VisionCommands;
import org.firstinspires.ftc.teamcode.commands.WaitCommands;
import org.firstinspires.ftc.teamcode.game.Config;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.opmodes.OpMode;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ConveyorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SortingSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;
import org.junit.Before;

public class TestHarness {
    @Before
    public void setUp() {
        hardwareMap = mock(HardwareMap.class);

        when(hardwareMap.get(any(Class.class), anyString()))
            .thenAnswer(
                invocation -> mock(
                    (Class<?>)invocation.getArgument(0)
                )
            );

        telemetry = mock(SampledTelemetry.class);

        opMode = mock(OpMode.class);

        gamepad1 = new GamepadEx(mock(Gamepad.class));
        gamepad2 = new GamepadEx(mock(Gamepad.class));

        Config.config = mock(Config.class);

        Subsystems.config = mock(ConfigSubsystem.class);
        Subsystems.nav = mock(NavSubsystem.class);
        Subsystems.drive = mock(DriveSubsystem.class);
        Subsystems.intake = mock(IntakeSubsystem.class);
        Subsystems.conveyor = mock(ConveyorSubsystem.class);
        Subsystems.gate = mock(GateSubsystem.class);
        Subsystems.deflector = mock(DeflectorSubsystem.class);
        Subsystems.flywheel = mock(FlywheelSubsystem.class);
        Subsystems.vision = mock(VisionSubsystem.class);
        Subsystems.vision.elementPose = new Pose(0, 0, 0);
        Subsystems.timing = mock(TimingSubsystem.class);
        Subsystems.sorting = mock(SortingSubsystem.class);

        playTimer = mock(ElapsedTime.class);

        Commands.wait = mock(WaitCommands.class, invocation -> new InstantCommand());
        Commands.config = mock(ConfigCommands.class, RETURNS_DEEP_STUBS);
        Commands.drive = mock(DriveCommands.class, RETURNS_DEEP_STUBS);
        Commands.intake = mock(IntakeCommands.class, RETURNS_DEEP_STUBS);
        Commands.conveyor = mock(ConveyorCommands.class, RETURNS_DEEP_STUBS);
        Commands.gate = mock(GateCommands.class, RETURNS_DEEP_STUBS);
        Commands.deflector = mock(DeflectorCommands.class, RETURNS_DEEP_STUBS);
        Commands.flywheel = mock(FlywheelCommands.class, RETURNS_DEEP_STUBS);
        Commands.vision = mock(VisionCommands.class, RETURNS_DEEP_STUBS);
        Commands.sorting = mock(SortingCommands.class, RETURNS_DEEP_STUBS);
    }

    public void input(Runnable runnable) {
        runnable.run();
        CommandScheduler.getInstance().run();
    }

    /** @noinspection SameParameterValue*/
    protected static <T> T mockDevice(Class<T> deviceType) {
        return mock(deviceType);
    }

    protected MotorEx mockMotor() {
        MotorEx motor = mock(MotorEx.class);
        motor.motorEx = mock(DcMotorEx.class);
        motor.motor = mock(DcMotor.class);
        return motor;
    }
}
