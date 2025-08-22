package org.firstinspires.ftc.teamcode.opmodes;

import static org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat.HTML;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.adaptations.ftcdashboard.SampledTelemetry;
import org.firstinspires.ftc.teamcode.commands.Commands;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public abstract class OpMode extends CommandOpMode {
    public static SampledTelemetry telemetry;
    public static OpMode opMode;
    public static Hardware hardware;
    public static GamepadEx gamepad1;
    public static GamepadEx gamepad2;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().disable();
        CommandScheduler.getInstance().reset();

        super.telemetry.setDisplayFormat(HTML);

        telemetry = new SampledTelemetry(
            new MultipleTelemetry(
                super.telemetry,
                FtcDashboard.getInstance().getTelemetry()
            )
        );

        opMode = this;
        hardware = new Hardware(super.hardwareMap);
        gamepad1 = new GamepadEx(super.gamepad1);
        gamepad2 = new GamepadEx(super.gamepad2);

        Subsystems.initialize();
        Commands.initialize();
    }

    @Override
    public void waitForStart() {
        while (!isStarted() && !isStopRequested()) {
            CommandScheduler.getInstance().run();
            Thread.yield();
        }

        Subsystems.config.start();
    }
    
    @Override
    public void reset() {
        super.reset();
        hardware = null;
    }
}
