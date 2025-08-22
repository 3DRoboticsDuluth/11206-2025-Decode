package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "My Teleop 3")
@SuppressWarnings("unused")
public class MyTeleOpMode extends OpMode {
    @Override
    public void initialize() {
        super.initialize();
        waitForStart();
    }
}
