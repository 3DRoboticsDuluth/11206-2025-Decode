package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.B;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_LEFT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_RIGHT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.LEFT_TRIGGER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

import com.seattlesolvers.solverslib.command.button.Trigger;

public class FlywheelControl {
    private static final double TRIGGER_THRESHOLD = 0.5;

    public FlywheelControl() {
        new Trigger(() -> gamepad1.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD
            || gamepad1.getTrigger(LEFT_TRIGGER) > TRIGGER_THRESHOLD)
                .whileActiveOnce(flywheel.start())
                .whenInactive(flywheel.stop());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(B))
            .and(gamepad2.getGamepadButton(DPAD_UP))
            .toggleWhenActive(flywheel.start());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(B))
            .and(gamepad2.getGamepadButton(DPAD_DOWN))
            .toggleWhenActive(flywheel.reverse());

        gamepad2.getGamepadButton(START).negate()
            .and(gamepad2.getGamepadButton(B))
            .and((gamepad2.getGamepadButton(DPAD_LEFT)).or(gamepad2.getGamepadButton(DPAD_RIGHT)))
            .toggleWhenActive(flywheel.stop());
    }
}
