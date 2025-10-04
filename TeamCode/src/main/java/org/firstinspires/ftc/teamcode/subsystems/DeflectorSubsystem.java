package org.firstinspires.ftc.teamcode.subsystems;


import static org.firstinspires.ftc.teamcode.opmodes.OpMode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.controls.DeflectorControl;

@Config
public class DeflectorSubsystem extends SubsystemBase {
    public DeflectorSubsystem() {
        hardware.deflector.setPosition(POSITION);
    }

    public void periodic() {
        hardware.deflector.setPosition(POSITION);
    }
    public static double DEFLECTOR_MAX = .54;
    public static double DEFLECTOR_MIN = .45;
    public static double POSITION = .5;
    public static double DEFLECTOR_POSITION;

    public void setPosition() {
        DEFLECTOR_POSITION = POSITION;
    }
}
