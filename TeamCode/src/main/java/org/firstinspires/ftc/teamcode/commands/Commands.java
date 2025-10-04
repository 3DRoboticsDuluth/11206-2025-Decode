package org.firstinspires.ftc.teamcode.commands;

public class Commands {
    public static WaitCommands wait;
    public static ConfigCommands config;
    public static DriveCommands drive;
    public static VisionCommands vision;
    public static AutoCommands auto;
    public static DepositCommands deposit;
    public static DeflectorCommands deflector;

    public static void initialize() {
        wait = new WaitCommands();
        config = new ConfigCommands();
        drive = new DriveCommands();
        vision = new VisionCommands();
        auto = new AutoCommands();
        deposit = new DepositCommands();
        deflector = new DeflectorCommands();
    }
}
