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
    public Pose pose = new Pose(0, 0, 0);
    public Alliance alliance = Alliance.UNKNOWN;
    public Side side = Side.UNKNOWN;
    public String quanomous = null;
    public double delay = 0;
    public double responsiveness = 0.5;
    public boolean robotCentric = false;
    public boolean goalLock = false;
    public double goalDistanceOffsetSouth = 18;
    public double goalDistanceOffsetNorth = 24;
    public double goalAngleOffsetSouth = 0;
    public double goalAngleOffsetNorth = 0;
}
