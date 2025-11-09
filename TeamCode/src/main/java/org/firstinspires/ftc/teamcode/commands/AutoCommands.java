package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.conveyor;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.flywheel;
import static org.firstinspires.ftc.teamcode.commands.Commands.gate;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SelectCommand;

public class AutoCommands {
    public Command execute() {
        return auto.delayStart().andThen(
            drive.toLaunchNear(),
            wait.seconds(3),
            drive.toSpike3(),
            wait.seconds(3),
            drive.toSpike2(),
            wait.seconds(3),
            drive.toSpike1(),
            wait.seconds(3),
            drive.toSpike0(),
            wait.seconds(3),
            drive.toLaunchFar(),
            wait.seconds(3),
            drive.toGate(),
            wait.seconds(3),
            drive.toBase()
        );
    }
    
    public Command delayStart() {
        return new SelectCommand(
            () -> wait.seconds(config.delay)
        );
    }

    public Command prepareDeposit() {
        return new SelectCommand(
            () -> gate.close().alongWith(
                flywheel.forward(),
                flywheel.isReady().andThen(
                    flywheel.wheelUpToSpeed()
                )
            )
        );
    }

    public Command depositStart() {
        return new SelectCommand(
            () -> conveyor.launch()
        );
    }

    public Command depositStop() {
        return new SelectCommand(
            () -> conveyor.stop().alongWith(
                flywheel.stop(),
                intake.stop()
            )
        );
    }

    public Command intakeStart() {
        return new SelectCommand(
            () -> intake.forward().alongWith(
                conveyor.forward(),
                gate.close(),
                flywheel.hold()
            )
        );
    }

    public Command intakeStop() {
        return new SelectCommand(
            () -> conveyor.reverse().andThen(
                wait.doherty(3),
                conveyor.stop(),
                intake.hold(),
                gate.open(),
                wait.doherty(2),
                flywheel.forward()
            )
        );
    }

    public Command spitArtifact() {
        return new SelectCommand(
            () -> intake.reverse().alongWith(
                conveyor.reverse(),
                gate.close()
            )
        );
    }

    public Command intakeSpike3() {
        return new SelectCommand(
            () -> drive.toSpike3().andThen(
                intakeStart(),
                drive.forward(16)
            )
        );
    }

    public Command intakeSpike2() {
        return new SelectCommand(
                () -> drive.toSpike2().andThen(
                        intakeStart(),
                        drive.forward(16)
                )
        );
    }

    public Command intakeSpike1() {
        return new SelectCommand(
                () -> drive.toSpike1().andThen(
                        intakeStart(),
                        drive.forward(16)
                )
        );
    }

    public Command intakeSpike0() {
        return new SelectCommand(
                () -> drive.toSpike0().andThen(
                        intakeStart(),
                        drive.forward(16)
                )
        );
    }

    public Command depositNear() {
        return new SelectCommand(
            () -> drive.toLaunchNear().alongWith(
                auto.prepareDeposit()
            ).andThen(
                auto.depositStart()
            )
        );
    }

    public Command depositFar() {
        return new SelectCommand(
            () -> drive.toLaunchFar().alongWith(
                prepareDeposit()
            ).andThen(
                auto.depositStart()
            )
        );
    }

    public Command depositFromPose() {
        return new SelectCommand(
            () -> drive.toLaunchAlign().alongWith(
                auto.prepareDeposit()
            ).andThen(
                auto.depositStart()
            )
        );
    }

    public Command releaseGate() {
        return new SelectCommand(
                () -> drive.toGate().andThen(
                    drive.forward(3),
                    wait.seconds(1),
                    drive.forward(-3)
                )
        );
    }

//    public Command humanPlayerZone() {
//        return new SelectCommand(
//            () -> drive.toLoadingZone().alongWith(
//                gate.close()
//            )
//        );
//    }
}
