package org.firstinspires.ftc.teamcode.commands;

public class Commands {
    public static WaitCommands wait;
    public static ConfigCommands config;
    public static DriveCommands drive;
    public static ConveyorCommands conveyor;
    public static GateCommands gate;
    public static DeflectorCommands deflector;
    public static FlywheelCommands flywheel;
    public static VisionCommands vision;
    public static AutoCommands auto;

    public static void initialize() {
        wait = new WaitCommands();
        config = new ConfigCommands();
        drive = new DriveCommands();
        conveyor = new ConveyorCommands();
        gate = new GateCommands();
        deflector = new DeflectorCommands();
        flywheel = new FlywheelCommands();
        vision = new VisionCommands();
        auto = new AutoCommands();
    }
}
