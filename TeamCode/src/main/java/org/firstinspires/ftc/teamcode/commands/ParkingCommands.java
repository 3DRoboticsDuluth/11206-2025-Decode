package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.parking;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;

import kotlin.comparisons.UComparisonsKt;

public class ParkingCommands {
    public Command park() {
        return complete(parking::park);
    }

    public Command unPark() {
        return complete(parking::unPark);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, deflector);
    }

}
