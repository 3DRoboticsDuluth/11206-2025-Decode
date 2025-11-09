package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.Y;
import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

public class DeflectorControls {
    public DeflectorControls() {
        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(Y))
            .and(gamepad2.getGamepadButton(DPAD_UP))
            .toggleWhenActive(deflector.up());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(Y))
            .and(gamepad2.getGamepadButton(DPAD_DOWN))
            .toggleWhenActive(deflector.down());
    }
}
