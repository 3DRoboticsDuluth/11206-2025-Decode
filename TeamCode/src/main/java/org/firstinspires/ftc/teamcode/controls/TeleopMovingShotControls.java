package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.*;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.*;
import static org.firstinspires.ftc.teamcode.commands.Commands.*;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

import com.seattlesolvers.solverslib.command.button.Trigger;


public class TeleopMovingShotControls {

    private static final double TRIGGER_THRESHOLD = 0.5;

    public TeleopMovingShotControls() {

        gamepad1.getGamepadButton(LEFT_BUMPER)
                .whileActiveOnce(movingShot.driverControlledShoot());

        new Trigger(() -> gamepad1.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD)
            .whenActive(movingShot.fireWhenReady());

        // Cycle aim modes (MANUAL -> DRIVER_ASSIST -> AUTO_AIM -> FULL_AUTO -> MANUAL)
        gamepad2.getGamepadButton(LEFT_BUMPER)
            .whenPressed(movingShot.cycleAimMode());

        // RIGHT_BUMPER: Toggle deposit target (NEAR <-> FAR)
        gamepad2.getGamepadButton(RIGHT_BUMPER)
            .whenPressed(movingShot.cycleDepositTarget());

        gamepad1.getGamepadButton(RIGHT_BUMPER)
            .whenPressed(movingShot.quickShot());

        gamepad1.getGamepadButton(Y)
            .whenPressed(movingShot.emergencyStop());
    }
}