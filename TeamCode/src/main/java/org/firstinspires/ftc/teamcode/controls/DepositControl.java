package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.LEFT_TRIGGER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.deposit;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;

import com.seattlesolvers.solverslib.command.button.Trigger;

public class DepositControl {

    private static final double TRIGGER_THRESHOLD = 0.5;

    public DepositControl() {
        new Trigger(() -> gamepad1.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD
            || gamepad1.getTrigger(LEFT_TRIGGER) > TRIGGER_THRESHOLD)
                .whileActiveOnce(deposit.launch())
                .whenInactive(deposit.stop());
    }
}
