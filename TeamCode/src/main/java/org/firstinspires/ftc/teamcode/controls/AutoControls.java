package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.A;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.B;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_LEFT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.DPAD_RIGHT;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.LEFT_BUMPER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.RIGHT_BUMPER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.START;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.X;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.LEFT_TRIGGER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.GOAL_ANGLE_INCREMENT;
import static org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.GOAL_DISTANCE_INCREMENT;

import com.seattlesolvers.solverslib.command.button.Trigger;

import org.firstinspires.ftc.teamcode.game.Config;

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

        //Config Controls

        gamepad2.getGamepadButton(DPAD_LEFT)
            .whenPressed(() -> Config.config.goalAngleOffsetNorth += GOAL_ANGLE_INCREMENT);

        gamepad2.getGamepadButton(DPAD_RIGHT)
            .whenPressed(() -> Config.config.goalAngleOffsetNorth -= GOAL_ANGLE_INCREMENT);

        gamepad2.getGamepadButton(B)
            .whenPressed(() -> Config.config.goalAngleOffsetSouth += GOAL_ANGLE_INCREMENT);

        gamepad2.getGamepadButton(X)
            .whenPressed(() -> Config.config.goalAngleOffsetSouth -= GOAL_ANGLE_INCREMENT);

        gamepad2.getGamepadButton(LEFT_BUMPER)
            .whenPressed(() -> Config.config.goalDistanceOffsetSouth -= GOAL_DISTANCE_INCREMENT );

        gamepad2.getGamepadButton(RIGHT_BUMPER)
            .whenPressed(() -> Config.config.goalDistanceOffsetSouth += GOAL_DISTANCE_INCREMENT);

        new Trigger(() -> gamepad2.getTrigger(LEFT_TRIGGER) > TRIGGER_THRESHOLD)
            .whenActive(() -> Config.config.goalDistanceOffsetNorth -= GOAL_DISTANCE_INCREMENT );

        new Trigger(() -> gamepad2.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD)
            .whenActive(() -> Config.config.goalDistanceOffsetNorth += GOAL_DISTANCE_INCREMENT);
    }
}
