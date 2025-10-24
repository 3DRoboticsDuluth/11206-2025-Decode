package org.firstinspires.ftc.teamcode.commands;

public class Commands {
    public static WaitCommands wait;
    public static ConfigCommands config;
    public static DriveCommands drive;
    public static ConveyorCommands conveyor;
    public static VisionCommands vision;
    public static AutoCommands auto;
    public static FlywheelCommands flywheel;

    public static void initialize() {
        wait = new WaitCommands();
        config = new ConfigCommands();
        drive = new DriveCommands();
        conveyor = new ConveyorCommands();
        vision = new VisionCommands();
        auto = new AutoCommands();
        flywheel = new FlywheelCommands();
    }
}
