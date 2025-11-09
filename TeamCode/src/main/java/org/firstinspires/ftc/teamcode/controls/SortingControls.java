package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.LEFT_TRIGGER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.sorting;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

import com.seattlesolvers.solverslib.command.button.Trigger;

public class SortingControls {
    private static final double TRIGGER_THRESHOLD = 0.5;

    public SortingControls() {
        new Trigger(() -> gamepad2.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD ||
            gamepad2.getTrigger(LEFT_TRIGGER) > TRIGGER_THRESHOLD)
            .whileActiveOnce(sorting.sort())
            .whenInactive(sorting.pass());
    }
}
