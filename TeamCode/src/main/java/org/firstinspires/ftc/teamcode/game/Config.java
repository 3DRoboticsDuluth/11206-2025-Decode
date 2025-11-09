package org.firstinspires.ftc.teamcode.game;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;

@Configurable
public class Config {
    public static Config config;
    public transient boolean auto;
    public transient boolean teleop;
    public transient boolean started;
    public transient boolean interrupt;
    public Alliance alliance = Alliance.UNKNOWN;
    public Side side = Side.UNKNOWN;
    public double delay = 0;
    public double responsiveness = 0.33;
    public boolean robotCentric = false;
    public String quanomous;
    public Pose pose = new Pose(0, 0, 0);
}
