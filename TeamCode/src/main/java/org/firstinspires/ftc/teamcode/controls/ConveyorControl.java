package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_LEFT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_RIGHT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.X;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

public class ConveyorControl {
    public ConveyorControl() {
        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(X))
            .and(gamepad2.getGamepadButton(DPAD_UP))
            .toggleWhenActive(conveyor.forward());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(X))
            .and(gamepad2.getGamepadButton(DPAD_DOWN))
            .toggleWhenActive(conveyor.reverse());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(X))
            .and((gamepad2.getGamepadButton(DPAD_LEFT))
                .or(gamepad2.getGamepadButton(DPAD_RIGHT)))
            .whenActive(conveyor.stop());
    }
}
