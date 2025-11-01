package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.A;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.B;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_DOWN;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_LEFT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_RIGHT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_UP;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.X;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.Y;
import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;

public class AutoControl {
    public AutoControl() {
        gamepad1.getGamepadButton(START).negate()
            .and(gamepad1.getGamepadButton(A))
            .and(gamepad1.getGamepadButton(DPAD_UP))
            .whenActive(auto.intakeArtifact());

        gamepad1.getGamepadButton(START).negate()
            .and(gamepad1.getGamepadButton(A))
            .and(gamepad1.getGamepadButton(DPAD_DOWN))
            .whenActive(auto.spitArtifact());

//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(A))
//            .and((gamepad1.getGamepadButton(DPAD_LEFT)).or(gamepad1.getGamepadButton(DPAD_RIGHT)))
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
//            .and((gamepad1.getGamepadButton(DPAD_LEFT)).or(gamepad1.getGamepadButton(DPAD_RIGHT)))
//            .whenActive(auto.depositFromPose());
//
//        gamepad1.getGamepadButton(START).negate()
//            .and(gamepad1.getGamepadButton(Y))
//            .whenActive(auto.humanPlayerZone());

        gamepad1.getGamepadButton(START).negate()
            .and(gamepad1.getGamepadButton(X))
            .and(gamepad1.getGamepadButton(DPAD_UP))
            .whenActive(auto.gateOpen());

        gamepad1.getGamepadButton(START).negate()
            .and(gamepad1.getGamepadButton(X))
            .and(gamepad1.getGamepadButton(DPAD_DOWN))
            .whenActive(auto.gateClose());
    }
}
