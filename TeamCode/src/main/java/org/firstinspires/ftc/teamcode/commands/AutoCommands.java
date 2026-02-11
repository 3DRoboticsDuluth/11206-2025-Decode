package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.TRANSPARENT;
import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.lights;
import static org.firstinspires.ftc.teamcode.commands.Commands.quanomous;
import static org.firstinspires.ftc.teamcode.commands.Commands.vision;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.adaptations.pedropathing.RepeatCommand;
import org.firstinspires.ftc.teamcode.game.Side;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;

import java.util.HashMap;

public class AutoCommands {
    public Command execute() {
//        return auto.delayStart().andThen(
//            quanomous.execute(),
//            wait.doherty(2)
//        ).withTimeout(29500).andThen(
//            auto.stop()
//        );
//
        return auto.delayStart().andThen(
            deposit(SOUTH, 0, 0),
            releaseGate(),
            gateIntake(),
            deposit(SOUTH,0,0)
        );
    }

    public Command executeChasing() {
        return auto.delayStart().andThen(
            /*quanomous.execute(),*/
            auto.deposit(NORTH, 0, 0),
            auto.chase(5),
        /*).withTimeout(2800).andThen(*/
            auto.park(true, NavSubsystem.Axial.CENTER, NavSubsystem.Lateral.CENTER),
            wait.doherty(2),
        /*).withTimeout(29500).andThen(*/
            auto.stop()
        );
    }

    public Command delayStart() {
        return new DeferredCommand(
            () -> wait.seconds(config.delay), null
        );
    }

    public Command intakeStart() {
        return auto.goalLock(false).alongWith(
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
                auto.intakeStart(),
                drive.untilDistance(TILE_WIDTH * -1.5),
                drive.setPowerIntake()
            )
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
        return auto.goalLock(false).alongWith(
            conveyor.stop(),
            flywheel.stop(),
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
                drive.untilDistance(side == NORTH ? -10 : -30).andThen(
                    drive.untilHeading(10),
                    side == NORTH ? drive.untilNotBusy() : wait.noop(),
                    side == NORTH ? drive.untilHeading(4).withTimeout(1000) : wait.noop(),
                    conveyor.waitUntilStopped(),
                    // TODO: Add flywheel.isReady() for NORTH?
                    auto.depositStart(),
                    wait.doherty(2),
                    auto.depositStop()
                )
            )
        );
    }

    public Command releaseGate() {
        return drive.toGate();
    }

    public Command gateIntake() {
        return intakeStart().alongWith(
            drive.setPowerHigh()).andThen(
            drive.toGateIntake(),
            wait.doherty(1),
            drive.setPowerLow(),
            drive.toGateIntakeDepart().withTimeout(400),
            drive.setPowerAuto()
        );
    }

    public Command goalLock(boolean enabled) {
        return vision.goalLock(enabled).alongWith(
            drive.goalLock(enabled)
        );
    }

    public Command chase(int cycles) {
        return vision.chaseLock(true).alongWith(
            lights.set(TRANSPARENT),
            new RepeatCommand(
                execution -> drive.toChase(execution).alongWith(
                    drive.untilDistance(-1.5 * TILE_WIDTH).andThen(drive.setPowerLow()),
                    wait.milliseconds(50).andThen(vision.resetElement()),
                    auto.intakeStart()
                ).withTimeout(2000 + 200L * execution).andThen(
                    wait.doherty(),
                    drive.setPowerAuto(),
                    auto.deposit(NORTH, -0.25 * TILE_WIDTH, config.alliance.sign * -0.0 * TILE_WIDTH)
                ), cycles
            )
        );
    }

    public Command park(boolean gate, NavSubsystem.Axial axial, NavSubsystem.Lateral lateral) {
        return drive.setPowerAuto().alongWith(
            drive.curve(
                nav.getParkingPose(config.parkGate, axial, lateral)
            )
        );
    }

    public Command stop() {
        return drive.goalLock(false).alongWith(
            drive.stop(),
            intake.stop(),
            conveyor.stop(),
            gate.close(),
            flywheel.stop()
        );
    }
}
