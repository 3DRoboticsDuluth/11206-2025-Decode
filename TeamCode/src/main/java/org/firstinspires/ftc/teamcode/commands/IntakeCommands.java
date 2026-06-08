package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;

public class IntakeCommands {
    public Command forward() {
        return complete(intake::forward);
    }

    public Command reverse() {
        return complete(intake::reverse);
    }

    public Command hold() {
        return complete(intake::hold);
    }

    public Command stop() {
        return complete(intake::stop);
    }

    public Command reset() {
        return complete(intake::reset);
    }

    public Command untilElement() {
        return new DeferredCommand(
            () -> {
                int target = intake.artifacts + 1;
                return wait.until(
                    () -> intake.artifacts == target || intake.full
                );
            }, null
        );
    }

    public Command untilFull() {
        return wait.until(() -> intake.full);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
