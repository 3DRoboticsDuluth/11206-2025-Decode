package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.LEFT_BUMPER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.RIGHT_BUMPER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static org.firstinspires.ftc.teamcode.commands.Commands.kickstand;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

public class KickstandControl {
    public KickstandControl() {
        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(LEFT_BUMPER))
            .and(gamepad2.getGamepadButton(RIGHT_BUMPER))
            .whenActive(kickstand.engage());
    }
}
