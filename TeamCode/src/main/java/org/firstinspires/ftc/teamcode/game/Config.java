package org.firstinspires.ftc.teamcode.game;

import static org.firstinspires.ftc.teamcode.game.Location.SPIKE_INTAKE;
import static org.firstinspires.ftc.teamcode.game.Location.SUBMERSIBLE_DEPOSIT;
import static org.firstinspires.ftc.teamcode.game.Zone.INNER;

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
    public int spikesTarget = 0;
    public int spikesActual = 0;
    public int elementsTarget = 0;
    public int samplesActual = 0;
    public int specimenActual = 0;
    public int ascentLevel = 2;
    public Zone ascentZone = INNER;
    public Location intake = SPIKE_INTAKE;
    public Location deposit = SUBMERSIBLE_DEPOSIT;
    public Basket basket = Basket.HIGH;
    public Chamber chamber = Chamber.HIGH;
    public Sample sample = Sample.ALLIANCE;
    public Submersible submersible;
    public Pose pose = new Pose(0, 0, 0);
}
