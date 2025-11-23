package org.firstinspires.ftc.teamcode.opmodes;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.game.Config.config;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.controls.Controls;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;

@Autonomous(name = "Auto", preselectTeleOp = "Teleop")
@SuppressWarnings("unused")
public class AutoOpMode extends OpMode {
    @Override
    public void initialize() {
        super.initialize();

        config.alliance = Alliance.UNKNOWN;
        config.side = Side.UNKNOWN;
        config.quanomous = null;
        config.delay = 0;
        config.robotCentric = false;

        Controls.initializeAuto();

        waitForStart();
        
        if (isStopRequested()) return;

        if (config.auto && config.alliance == Alliance.UNKNOWN || config.side == Side.UNKNOWN)
            throw new RuntimeException("Alliance and/or Side is null");

        schedule(
            auto.execute()
        );
    }
}
