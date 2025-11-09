package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.A;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.B;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_LEFT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_RIGHT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.Y;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.LEFT_TRIGGER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;

import com.seattlesolvers.solverslib.command.button.Trigger;

public class AutoControls {
    private static final double TRIGGER_THRESHOLD = .5;
    public AutoControls() {
//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(A))
//            .and(gamepad1.getGamepadButton(DPAD_UP))
//            .whenActive(auto.intakeStart());
//
//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(A))
//            .and(gamepad1.getGamepadButton(DPAD_DOWN))
//            .whenActive(auto.spitArtifact());
//
//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(A))
//            .and((gamepad1.getGamepadButton(DPAD_LEFT))
//                .or(gamepad1.getGamepadButton(DPAD_RIGHT)))
//            .whenActive(auto.autoArtifact());
//
//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(B))
//            .and(gamepad1.getGamepadButton(DPAD_UP))
//            .whenActive(auto.depositNear());
//
//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(B))
//            .and(gamepad1.getGamepadButton(DPAD_DOWN))
//            .whenActive(auto.depositFar());
//
//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(B))
//            .and((gamepad1.getGamepadButton(DPAD_LEFT))
//                .or(gamepad1.getGamepadButton(DPAD_RIGHT)))
//            .whenActive(auto.depositFromPose());

        new Trigger(() -> gamepad1.getTrigger(LEFT_TRIGGER) > TRIGGER_THRESHOLD)
//            .toggleWhenActive(auto.intakeStart(), auto.intakeStop());
            .whenActive(auto.intakeStart())
            .whenInactive(auto.intakeStop());

        new Trigger(() -> gamepad1.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD)
            .whenActive(auto.depositStart())
            .whenInactive(auto.depositStop());

//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(Y))
//            .whenActive(auto.humanPlayerZone());
    }
}
