package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button.*;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.*;
import static org.firstinspires.ftc.teamcode.commands.Commands.*;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;

import com.seattlesolvers.solverslib.command.button.Trigger;


public class TeleopMovingShotControls {
    
    private static final double TRIGGER_THRESHOLD = 0.5;
    
    public TeleopMovingShotControls() {

        new Trigger(() -> gamepad1.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD)
            .whenActive(movingShot.fireWhenReady());

        // MANUAL -> DRIVER_ASSIST -> AUTO_AIM -> FULL_AUTO -> MANUAL
        gamepad1.getGamepadButton(LEFT_BUMPER)
            .whenPressed(movingShot.cycleAimMode());
        
        // Right bumper: Quick shot (fire immediately, don't wait for perfect)
        gamepad1.getGamepadButton(RIGHT_BUMPER)
            .whenPressed(movingShot.quickShot());

        // Y button: E-stop
        gamepad1.getGamepadButton(Y)
            .whenPressed(movingShot.emergencyStop());
    }
}