package org.firstinspires.ftc.teamcode.opmodes;

import static org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat.HTML;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.teamcode.adaptations.telemetry.SampledTelemetry;
import org.firstinspires.ftc.teamcode.commands.Commands;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public abstract class OpMode extends CommandOpMode {
    public static SampledTelemetry telemetry;
    public static OpMode opMode;
    public static GamepadEx gamepad1;
    public static GamepadEx gamepad2;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().disable();
        CommandScheduler.getInstance().reset();

        super.telemetry.setDisplayFormat(HTML);

        telemetry = new SampledTelemetry(
            new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), super.telemetry)
        );

        BlocksOpModeCompanion.hardwareMap = this.hardwareMap;

        opMode = this;
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
}
