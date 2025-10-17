package org.firstinspires.ftc.teamcode.game;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;

/**
 * ExampleRobotActions
 *
 * Skeleton implementation showing how to adapt your existing robot code to
 * the `RobotActions` interface. Replace TODOs with real subsystem calls.
 */
public class ExampleRobotActions implements RobotActions {

    @Override
    public void driveToCoords(double x, double y, int heading) {
        drive.to(x, y, heading);
    }

    @Override
    public void driveToTile(int tileX, int tileY, int heading) {
        double worldX = tileX * TILE_WIDTH;
        double worldY = tileY * TILE_WIDTH;
        driveToCoords(worldX, worldY, heading);
    }

    @Override
    public void intakeRow(int row) {
        // TODO: call subsystem, e.g. intake.intakeRow(row);
    }

    @Override
    public void depositMode(String mode, boolean sorted) {
        // TODO: call deposit routine (near vs far)
    }

    @Override
    public void depositTile(int tileX, int tileY, int heading, boolean sorted) {
        // Drive to tile then deposit
        driveToTile(tileX, tileY, heading);
        // TODO: perform deposit mechanism
    }

    @Override
    public void releaseGate() {
        // TODO: actuate gate
    }

    @Override
    public void delayMs(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    @Override
    public void launch() {
        // TODO: call shooter/launcher
    }

    @Override
    public void waitForCompletion() {
        // Optionally block until subsystems are idle (override to wait)
    }
}