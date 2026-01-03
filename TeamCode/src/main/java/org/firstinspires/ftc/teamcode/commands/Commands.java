package org.firstinspires.ftc.teamcode.commands;

public class Commands {
    public static WaitCommands wait;
    public static ConfigCommands config;
    public static DriveCommands drive;
    public static IntakeCommands intake;
    public static ConveyorCommands conveyor;
    public static SortingCommands sorting;
    public static GateCommands gate;
    public static DeflectorCommands deflector;
    public static FlywheelCommands flywheel;
    public static VisionCommands vision;
    public static KickstandCommands kickstand;
    public static QuanomousCommands quanomous;
    public static AutoCommands auto;

    public static void initialize() {
        wait = new WaitCommands();
        config = new ConfigCommands();
        drive = new DriveCommands();
        intake = new IntakeCommands();
        conveyor = new ConveyorCommands();
        sorting = new SortingCommands();
        gate = new GateCommands();
        deflector = new DeflectorCommands();
        flywheel = new FlywheelCommands();
        vision = new VisionCommands();
        kickstand = new KickstandCommands();
        quanomous = new QuanomousCommands();
        auto = new AutoCommands();
    }
}
