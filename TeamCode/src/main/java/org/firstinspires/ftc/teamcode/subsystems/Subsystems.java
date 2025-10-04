package org.firstinspires.ftc.teamcode.subsystems;

public class Subsystems {
    public static ConfigSubsystem config;
    public static NavSubsystem nav;
    public static DriveSubsystem drive;
    public static VisionSubsystem vision;
    public static TimingSubsystem timing;
    public static DepositSubsystem deposit;
    public static DeflectorSubsystem deflector;

    public static void initialize() {
        config = new ConfigSubsystem();
        deposit = new DepositSubsystem();
        deflector = new DeflectorSubsystem();
//        nav = new NavSubsystem();
//        drive = new DriveSubsystem();
//        vision = new VisionSubsystem();
//        timing = new TimingSubsystem();
    }
}
