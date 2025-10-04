package org.firstinspires.ftc.teamcode.subsystems;


import com.acmerobotics.dashboard.config.Config;

@Config
public class DeflectorSubsystem {
    public static double DEFLECTOR_MAX = .6;
    public static double DEFLECTOR_MIN = .4;
    public static double POSITION = .5;
    public static double DEFLECTOR_POSITION;

    public void setPosition() {
        DEFLECTOR_POSITION = POSITION;
    }
}
