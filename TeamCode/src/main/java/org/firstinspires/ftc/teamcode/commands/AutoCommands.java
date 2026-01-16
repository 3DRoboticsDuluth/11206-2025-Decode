package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.quanomous;
import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;

import static java.lang.Math.toRadians;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;

import java.util.HashMap;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
             quanomous.execute()
        ).withTimeout(29500).andThen(
             auto.stop().asProxy()
        );
    }

    public Command driveCurve() {
        return drive.curve(
             new Pose (1.5 * TILE_WIDTH, 0.5 * TILE_WIDTH, 0),
             new Pose (0 * TILE_WIDTH, 1.5 * TILE_WIDTH, 0),
             new Pose (-1.5 * TILE_WIDTH, 0.5 * TILE_WIDTH, 0)
        );
    }

    public Command driveCurves() {
        return drive.curves(
             new Pose (-1.5 * TILE_WIDTH, 0.5 * TILE_WIDTH, toRadians(135)),
             new Pose (0 * TILE_WIDTH, 1.5 * TILE_WIDTH, toRadians(135)),
             new Pose (1.5 *TILE_WIDTH, 0.5 * TILE_WIDTH, toRadians(135))
        );
    }

    public Command delayStart() {
        return new SelectCommand(
            () -> wait.seconds(config.delay)
        );
    }

    public Command intakeStart() {
        return auto.goalLock(false).alongWith(
            intake.forward(),
            conveyor.forward(),
            gate.close(),
            flywheel.hold()
        );
    }

    public Command intakeStop() {
        return conveyor.reverse().andThen(
            wait.doherty(2),
            conveyor.stop(),
            intake.hold(),
            gate.open()
        ).alongWith(
            wait.doherty(2).andThen(
                auto.goalLock(true).andThen(
                    flywheel.forward(),
                    flywheel.isReady(),
                    drive.rumble()
                )
            )
        );
    }

    public Command intake(double distance) {
        return auto.intakeStart().andThen(
            drive.forward(distance).withTimeout(1500),
            drive.setPowerAuto()
        );
    }

    public Command intakeSpike(int spike) {
        return new SelectCommand(
            new HashMap<Object, Command>() {{
                put(1, drive.toSpike1());
                put(2, drive.toSpike2());
                put(3, drive.toSpike3());
            }}, () -> spike
        ).alongWith(
             wait.doherty().andThen(
                 drive.toDistance(TILE_WIDTH * -1),
                 auto.intakeStart()
             )
        );
    }

    public Command intakeSpike0() {
        return drive.toSpike0Approach().andThen(
            drive.toSpike0(),
            drive.setPowerMedium(),
            drive.turn(config.alliance.sign * 25).withTimeout(500),
            auto.intake(7).withTimeout(750),
            drive.turn(config.alliance.sign * 5).withTimeout(500),
            auto.intake(16.7)
        );
    }

    public Command depositStart() {
        return auto.goalLock(true).andThen(
            intake.forward(),
            flywheel.forward(),
//            wait.doherty(config.auto ? 2 : 0),
            flywheel.isReady(),
            conveyor.launch()
        );
    }

    public Command depositStop() {
        return auto.goalLock(false).alongWith(
            conveyor.stop(),
            flywheel.stop(),
            intake.stop()
        );
    }

    public Command depositSouth(double axialOffset, double lateralOffset) {
        return drive.toDepositSouth(axialOffset, lateralOffset).alongWith(
            auto.intakeStop(),
            drive.toDistance(-6).andThen(
                auto.deposit()
            )
        );
    }

    public Command depositNorth(double axialOffset, double lateralOffset) {
        return drive.toDepositNorth(axialOffset, lateralOffset).alongWith(
            auto.intakeStop(),
            drive.toDistance(-6).andThen(
                auto.deposit()
            )
        );
    }

    public Command deposit() {
        return auto.fork(
            auto.depositStart().andThen(
                wait.doherty(2),
                auto.depositStop()
            )
        ).andThen(
            wait.doherty(1)
        );
    }

    public Command releaseGate() {
        return drive.toGate().andThen(
            drive.forward(32).withTimeout(3000),
            wait.seconds(1),
            drive.toGate()
        );
    }

    public Command goalLock(boolean enabled) {
        return drive.goalLock(enabled).alongWith(
            vision.goalLock(enabled)
        );
    }

    public Command stop() {
        return drive.stop().alongWith(
            intake.stop(),
            conveyor.stop(),
            flywheel.stop()
        );
    }

    public Command fork(Command command) {
        return new InstantCommand(command::schedule);
    }
}
