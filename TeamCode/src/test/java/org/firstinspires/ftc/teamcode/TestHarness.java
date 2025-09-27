package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.hardware;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.opMode;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.playTimer;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
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
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;
import org.junit.Before;

public class TestHarness {
    @Before
    public void setUp() {
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

    private MotorEx mockMotor() {
        MotorEx motor = mock(MotorEx.class);
        motor.motorEx = mock(DcMotorEx.class);
        return motor;
    }
}
