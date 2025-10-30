package org.firstinspires.ftc.teamcode.controls;

import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.LEFT_TRIGGER;
import static com.seattlesolvers.solverslib.gamepad.GamepadKeys.Trigger.RIGHT_TRIGGER;
import static org.firstinspires.ftc.teamcode.commands.Commands.sorting;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;

import com.seattlesolvers.solverslib.command.button.Trigger;

public class SortingControl {
    private static final double TRIGGER_THRESHOLD = .5;
    public SortingControl() {
        new Trigger(() -> gamepad2.getTrigger(RIGHT_TRIGGER) > TRIGGER_THRESHOLD)
                .whileActiveOnce(sorting.sort())
                .whenInactive(sorting.pass());

        new Trigger(() -> gamepad2.getTrigger(LEFT_TRIGGER) > TRIGGER_THRESHOLD)
                .whileActiveOnce(sorting.sort())
                .whenInactive(sorting.pass());
    }
}
