package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.BACK;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

public class GateControls {
    public GateControls() {
        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(BACK))
            .and(gamepad2.getGamepadButton(DPAD_UP))
            .whenActive(gate.open());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(BACK))
            .and(gamepad2.getGamepadButton(DPAD_DOWN))
            .whenActive(gate.close());
    }
}
