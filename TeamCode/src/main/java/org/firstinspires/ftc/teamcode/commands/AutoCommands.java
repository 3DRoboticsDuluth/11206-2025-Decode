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
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.pedropathing.RepeatCommand;
import org.firstinspires.ftc.teamcode.game.Side;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystems;

import java.util.HashMap;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
            quanomous.execute(),
            auto.stop()
        ).withTimeout(29500).andThen(
            auto.stop()
        );
    }

    public Command delayStart() {
        return new DeferredCommand(
            () -> wait.seconds(config.delay), null
        );
    }

    public Command intakeStart() {
        return auto.goalLock(false).andThen(
            intake.forward(),
            conveyor.forward(),
            gate.close()
        );
    }

    public Command intakeStop() {
        return auto.goalLock(true).andThen(
            flywheel.forward(),
            conveyor.reverse(),
            gate.hold(),
            wait.doherty(2),
            conveyor.stop(),
            intake.hold()
        );
    }

    public Command intake(int spike) {
        return new SelectCommand(
            new HashMap<Object, Command>() {{
                put(0, drive.toSpike0());
                put(1, drive.toSpike1());
                put(2, drive.toSpike2());
                put(3, drive.toSpike3());
            }}, () -> spike
        ).alongWith(
            wait.doherty(2).andThen(
                auto.depositStop(),
                auto.intakeStart(),
                drive.untilDistance(TILE_WIDTH * -2),
                drive.setPowerIntake()
            )
        ).andThen(
            wait.doherty(0.5)
        );
    }

    public Command depositStart() {
        return auto.goalLock(true).andThen(
            gate.open(),
            intake.forward(),
            flywheel.forward(),
            conveyor.launch()
        );
    }

    public Command depositStop() {
        return auto.goalLock(false).andThen(
            conveyor.stop(),
            flywheel.stop(),
            intake.reset(),
            intake.stop()
        );
    }

    public Command deposit(Side side, double axialOffset, double lateralOffset) {
        return auto.intakeStop().alongWith(
            drive.setPowerAuto(),
            new SelectCommand(
                new HashMap<Object, Command>() {{
                    put(NORTH, drive.toDepositNorth(axialOffset, lateralOffset));
                    put(SOUTH, drive.toDepositSouth(axialOffset, lateralOffset));
                }}, () -> side
            ).alongWith(
                drive.untilDistance(side == NORTH ? -9 : (config.pose.x < TILE_WIDTH * -2 ? -24 : -48)).andThen(
                    drive.untilHeading(config.pose.x > TILE_WIDTH * 2 ? 22 : 15),
                    side == NORTH ? drive.untilNotBusy() : wait.noop(),
                    side == NORTH ? drive.untilHeading(4).withTimeout(1000) : wait.noop(),
                    side == NORTH ? flywheel.isReady() : wait.noop(),
                    auto.depositStart(),
                    wait.doherty(1)
                )
            )
        );
    }

    public Command releaseGate() {
        return drive.toGate().alongWith(
            gate.close()
        );
    }

    public Command gateIntake() {
        return auto.intakeStart().alongWith(
            drive.toGate().andThen(
                drive.setPowerHigh(),
                drive.toGateIntake().withTimeout(1500),
                wait.seconds(1.5),
                wait.until (() -> Subsystems.intake.full).withTimeout(2000),
                drive.setPowerLow(),
                drive.toGateIntakeDepart().withTimeout(400),
                drive.setPowerAuto()
            )
        );
    }

    public Command goalLock(boolean enabled) {
        return vision.goalLock(enabled).alongWith(
            drive.goalLock(enabled)
        );
    }

    public Command drive(Pose pose) {
        return drive.curve(pose).alongWith(
            auto.depositStop()
        );
    }

    public Command chase(int cycles) {
        return intake.reset().andThen(
            new RepeatCommand(
                e1 -> new RepeatCommand(
                    e2 -> drive.toChaseScan().alongWith(
                        vision.chaseLock(true),
                        vision.waitForElement().withTimeout(3000).andThen(
                            auto.intakeStart(),
                            drive.chaseLock(true),
                            intake.waitForElement().raceWith(
                                drive.untilStill(0.4)
                            ), vision.resetElement()
                        )
                    ), attempts -> Subsystems.intake.full || attempts >= 4 || (
                        Subsystems.intake.artifacts >= 2 &&
                        Subsystems.nav.getDepositNorthPoseDistance() <= TILE_WIDTH * 2
                    ) || (
                        Subsystems.intake.artifacts >= 1 &&
                        Subsystems.nav.getDepositNorthPoseDistance() <= TILE_WIDTH * 1
                    )
                ).andThen(
                    drive.chaseLock(false),
                    auto.deposit(config.side, 0, 0),
                    wait.doherty(2),
                    auto.depositStop()
                ), cycles
            )
        );
    }

    public Command clusterChase() {
        return chase(Integer.MAX_VALUE);
    }

    /** @noinspection unused*/
    public Command park(boolean gate, NavSubsystem.Axial axial, NavSubsystem.Lateral lateral) {
        return wait.until(() -> !config.goalLock).withTimeout(800).andThen(
            drive.setPowerAuto().alongWith(
                drive.toParking(config.parkGate, axial, lateral)
            ).andThen(
                auto.stop()
            )
        );
    }

    public Command stop() {
        return drive.goalLock(false).alongWith(
            drive.chaseLock(false),
            drive.stop(),
            intake.stop(),
            conveyor.stop(),
            gate.close(),
            flywheel.stop()
        );
    }
}
