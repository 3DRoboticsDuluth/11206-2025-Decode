package org.firstinspires.ftc.teamcode.subsystems;

import android.hardware.lights.Light;

public class Subsystems {
    public static ConfigSubsystem config;
    public static NavSubsystem nav;
    public static DriveSubsystem drive;
    public static IntakeSubsystem intake;
    public static ConveyorSubsystem conveyor;
    public static SortingSubsystem sorting;
    public static GateSubsystem gate;
    public static DeflectorSubsystem deflector;
    public static FlywheelSubsystem flywheel;
    public static VisionSubsystem vision;
    public static KickstandSubsystem kickstand;
    public static TimingSubsystem timing;
    public static LightSubsystem light;

    public static void initialize() {
        config = new ConfigSubsystem();
        nav = new NavSubsystem();
        drive = new DriveSubsystem();
        intake = new IntakeSubsystem();
        conveyor = new ConveyorSubsystem();
        sorting = new SortingSubsystem();
        gate = new GateSubsystem();
        deflector = new DeflectorSubsystem();
        flywheel = new FlywheelSubsystem();
        vision = new VisionSubsystem();
        kickstand = new KickstandSubsystem();
        timing = new TimingSubsystem();
        light = new LightSubsystem();
    }
}
