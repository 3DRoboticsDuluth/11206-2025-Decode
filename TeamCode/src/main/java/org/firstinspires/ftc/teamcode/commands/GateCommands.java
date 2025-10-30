package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.gate;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SelectCommand;

public class GateCommands {
    public Command open() {
        return complete(gate::open);
    }

    public Command close() {
        return complete(gate::close);
    }

    private Command complete(Runnable runnable) {
        return new InstantCommand(runnable, gate);
    }

    public Command gateOpen() {
        return new SelectCommand(
                () -> Commands.gate.open()
        );
    }

    public Command gateClose() {
        return new SelectCommand(
                () -> Commands.gate.close()
        );
    }
}
