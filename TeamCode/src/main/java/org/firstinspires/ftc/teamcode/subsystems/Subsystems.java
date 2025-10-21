package org.firstinspires.ftc.teamcode.subsystems;

public class Subsystems {
    public static ConfigSubsystem config;
    public static NavSubsystem nav;
    public static DriveSubsystem drive;
    public static IntakeSubsystem intake;
    public static DeflectorSubsystem deflector;
    public static FlywheelSubsystem flywheel;
    public static VisionSubsystem vision;
    public static TimingSubsystem timing;
    public static GateSubsystem gate;

    public static void initialize() {
        config = new ConfigSubsystem();
        nav = new NavSubsystem();
        drive = new DriveSubsystem();
        intake = new IntakeSubsystem();
        deflector = new DeflectorSubsystem();
        flywheel = new FlywheelSubsystem();
        vision = new VisionSubsystem();
        timing = new TimingSubsystem();
        gate = new GateSubsystem();
    }
}
