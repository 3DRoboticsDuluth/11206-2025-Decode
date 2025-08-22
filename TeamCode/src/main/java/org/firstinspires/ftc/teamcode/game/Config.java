package org.firstinspires.ftc.teamcode.game;

@com.acmerobotics.dashboard.config.Config
public class Config {
    public static Config config;
    public transient boolean auto;
    public transient boolean teleop;
    public transient boolean started;
    public transient boolean interrupt;
    public Alliance alliance = Alliance.UNKNOWN;
    public Side side = Side.UNKNOWN;
    public double delay = 0;
    public double responsiveness = 0.25;
    public boolean robotCentric = true;
    public Pose pose = new Pose(0, 0, 0);
}
