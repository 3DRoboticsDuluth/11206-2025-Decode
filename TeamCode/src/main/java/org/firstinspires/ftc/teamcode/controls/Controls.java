package org.firstinspires.ftc.teamcode.controls;

public class Controls {
    public static void initializeAuto() {
        new ConfigControl();
    }

    public static void initializeTeleop() {
        new ConfigControl();
        new DriveControl();
        new IntakeControl();
        new DeflectorControl();
        new FlywheelControl();
        new AutoControl();
        new GateControl();
        new KickstandControl();
    }
}
