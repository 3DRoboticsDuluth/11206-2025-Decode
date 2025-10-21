package org.firstinspires.ftc.teamcode.commands;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.conveyor;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.robocol.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

public class ConveyorCommands {
    public Command forward(){
        return complete(conveyor::forward);
    }

    public Command reverse(){
        return complete(conveyor::reverse);
    }

    public Command stop(){
        return complete(conveyor::stop);
    }

    private Command complete(Runnable runnable) {
        return new SelectCommand(
                () -> new InstantCommand(runnable, conveyor)
//        ).andThen(
//                wait.until(() -> !drive.isBusy() || compare(drive.drive.pose, targetPose, true, MecanumDrive.PARAMS.errorPosition))
        );
    }
}
