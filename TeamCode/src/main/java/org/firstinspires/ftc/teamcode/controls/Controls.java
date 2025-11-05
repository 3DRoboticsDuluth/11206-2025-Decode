package org.firstinspires.ftc.teamcode.controls;

public class Controls {
    public static void initializeAuto() {
        new ConfigControl();
    }

    public static void initializeTeleop() {
        new ConfigControl();
        new DriveControl();
        new IntakeControl();
        new GateControl();
        new DeflectorControl();
        new FlywheelControl();
        new KickstandControl();
        new AutoControl();
    }
}
