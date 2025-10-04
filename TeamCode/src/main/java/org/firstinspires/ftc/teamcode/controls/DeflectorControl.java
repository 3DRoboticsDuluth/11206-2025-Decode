package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.A;
import static org.firstinspires.ftc.teamcode.commands.Commands.deflector;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;

public class DeflectorControl {
    public DeflectorControl() {
        gamepad1.getGamepadButton(A)
            .toggleWhenActive(deflector.setPosition());
    }
}
