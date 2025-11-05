package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.A;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_LEFT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_RIGHT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

public class IntakeControl {
    public IntakeControl() {
        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(A))
            .and(gamepad2.getGamepadButton(DPAD_UP))
            .toggleWhenActive(intake.forward());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(A))
            .and(gamepad2.getGamepadButton(DPAD_DOWN))
            .toggleWhenActive(intake.reverse());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(A))
            .and((gamepad2.getGamepadButton(DPAD_LEFT))
                .or(gamepad2.getGamepadButton(DPAD_RIGHT)))
            .toggleWhenActive(intake.stop());
    }
}
