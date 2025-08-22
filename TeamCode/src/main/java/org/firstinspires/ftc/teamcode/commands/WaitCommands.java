package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad1;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;

import java.util.function.BooleanSupplier;

public class WaitCommands {
    public Command seconds(double value) {
        return milliseconds((long)(value * 1000));
    }

    public Command milliseconds(long value) {
        return new WaitCommand(value);
    }

    public Command until(BooleanSupplier condition) {
        return new WaitUntilCommand(condition);
    }

    public Command forInterruptA() {
        return drive.rumble1().andThen(
            wait.until(
                () -> (config.auto && config.started) ||
                    (!config.interrupt && config.started) ||
                    (!gamepad1.gamepad.start && gamepad1.gamepad.a)
            )
        );
    }

    public Command forInterruptB() {
        return drive.rumble1().andThen(
            wait.until(
                () -> (config.auto && config.started) ||
                    (!config.interrupt && config.started) ||
                    (!gamepad1.gamepad.start && gamepad1.gamepad.b)
            )
        );
    }
    
    public Command doherty() {
        return doherty(1);
    }
    
    public Command doherty(double count) {
        return wait.seconds(0.4 * count);
    }

    public Command noop() {
        return wait.seconds(0);
    }
    
    /** @noinspection unused*/
    public Command debug() {
        return until(() -> gamepad1.gamepad.back);
    }
}
