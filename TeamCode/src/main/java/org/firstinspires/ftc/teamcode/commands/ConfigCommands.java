package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Location.BASKET_DEPOSIT;
import static org.firstinspires.ftc.teamcode.game.Location.OBSERVATION_SAMPLE;
import static org.firstinspires.ftc.teamcode.game.Location.SUBMERSIBLE_DEPOSIT;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.gamepad2;
import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.game.Basket;
import org.firstinspires.ftc.teamcode.game.Chamber;
import org.firstinspires.ftc.teamcode.game.Location;
import org.firstinspires.ftc.teamcode.game.Sample;
import org.firstinspires.ftc.teamcode.game.Submersible;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

public class ConfigCommands {
    public Command setEditable(boolean editable) {
        return complete(
            () -> Subsystems.config.setEditable(editable)
        );
    }

    public Command changeItem(ConfigSubsystem.Change change) {
        return complete(
            () -> Subsystems.config.changeItem(change)
        );
    }

    public Command changeValue(ConfigSubsystem.Change change) {
        return complete(
            () -> Subsystems.config.changeValue(change)
        );
    }

    public Command setIntakeAndDeposit(Location intake, Location deposit) {
        return setIntake(intake).andThen(
            setDeposit(deposit)
        );
    }

    public Command setIntakeAndDeposit(Location intake, Basket basket) {
        return setIntake(intake).andThen(
            setDeposit(basket)
        );
    }

    public Command setIntakeAndDeposit(Location intake, Chamber chamber) {
        return setIntake(intake).andThen(
            setDeposit(chamber)
        );
    }

    public Command setInterrupt(boolean interrupt) {
        return complete(
            () -> config.interrupt = interrupt
        ).alongWith(
            interrupt ? drive.setPowerLow() : drive.setPowerMedium()
        );
    }

    public Command setSample(Sample sample) {
        return complete(
            () -> config.sample = sample
        );
    }

    public Command setIntake(Location location) {
        return complete(
            () -> config.intake = location
        );
    }

    public Command setDeposit(Location location) {
        return complete(
            () -> {
                config.deposit = location;
                config.basket = null;
            }
        );
    }

    public Command setDeposit(Basket basket) {
        return complete(
            () -> {
                config.deposit = basket == null ? OBSERVATION_SAMPLE : BASKET_DEPOSIT;
                config.basket = basket;
            }
        );
    }

    public Command setDeposit(Chamber chamber) {
        return complete(
            () -> {
                config.deposit = SUBMERSIBLE_DEPOSIT;
                config.chamber = chamber;
            }
        );
    }

    public Command incrementSpikes() {
        return complete(
            () -> config.spikesActual++
        );
    }
    
    public Command incrementSamples() {
        return complete(
            () -> config.samplesActual++
        );
    }

    public Command incrementSpecimen() {
        return complete(
            () -> config.specimenActual++
        );
    }

    public Command complete(Runnable runnable) {
        return new SelectCommand(
            () -> new InstantCommand(runnable, Subsystems.config)
        );
    }

    public Command setSubmersible() {
        return complete(
            () -> config.submersible = Submersible.getFromAngle(
                toDegrees(
                    atan2(
                        -gamepad2.gamepad.left_stick_x,
                        -gamepad2.gamepad.left_stick_y
                    )
                )
            )
        );
    }
}
