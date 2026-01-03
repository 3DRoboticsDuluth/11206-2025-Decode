package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.BACK;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_LEFT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_RIGHT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.LEFT_STICK_BUTTON;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.RIGHT_STICK_BUTTON;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;

import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public class DriveControls {
    public DriveControls() {
        Subsystems.drive.setDefaultCommand(
            drive.input(
                () -> -gamepad1.gamepad.left_stick_y,
                () -> -gamepad1.gamepad.left_stick_x,
                () -> -gamepad1.gamepad.right_stick_x
            )
        );

        gamepad1.getGamepadButton(DPAD_DOWN)
            .whenActive(drive.setPowerLow());

        gamepad1.getGamepadButton(DPAD_LEFT)
            .or(gamepad1.getGamepadButton(DPAD_RIGHT))
            .whenActive(drive.setPowerMedium());

        gamepad1.getGamepadButton(DPAD_UP)
            .whenActive(drive.setPowerHigh());

        gamepad1.getGamepadButton(BACK)
            .and(gamepad1.getGamepadButton(START))
            .whenActive(() -> config.robotCentric = !config.robotCentric);

        gamepad1.getGamepadButton(LEFT_STICK_BUTTON)
            .and(gamepad1.getGamepadButton(RIGHT_STICK_BUTTON))
            .whenActive(drive.toStart());
    }
}
