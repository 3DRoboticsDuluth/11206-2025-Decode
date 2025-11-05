package org.firstinspires.ftc.teamcode.controls;

public class Controls {
    public static void initializeAuto() {
        new ConfigControls();
    }

    public static void initializeTeleop() {
        new ConfigControls();
        new DriveControls();
        new IntakeControls();
        new ConveyorControls();
        new SortingControls();
        new GateControls();
        new DeflectorControls();
        new FlywheelControls();
        new KickstandControls();
        new AutoControls();
    }
}
