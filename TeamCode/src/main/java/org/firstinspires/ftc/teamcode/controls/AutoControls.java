package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.A;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.B;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.LEFT_TRIGGER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

import com.seattlesolvers.solverslib.command.button.Trigger;

public class AutoControls {
    private static final double TRIGGER_THRESHOLD = .5;
    public AutoControls() {
        new Trigger(() -> gamepad1.getTrigger(LEFT_TRIGGER) > TRIGGER_THRESHOLD)
            .whenActive(auto.intakeStart())
            .whenInactive(auto.intakeStop());

        new Trigger(() -> gamepad1.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD)
            .whenActive(auto.depositStart())
            .whenInactive(auto.depositStop());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(A))
            .toggleWhenActive(auto.goalLock(true), auto.goalLock(false));

//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(Y))
//            .whenActive(auto.humanPlayerZone());
    }
}
