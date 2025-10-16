package org.firstinspires.ftc.teamcode.game;

/**
 * RobotActions
 *
 * Define the actions your robot runtime exposes. Implement this interface
 * to adapt your drivetrain, shooter, intake, and other subsystems so the
 * PlanExecutor can call them.
 */
public interface RobotActions {

    // Drive to world coordinates (units you use)
    void driveToCoords(double x, double y, int heading);

    // Drive to a tile coordinate (discrete grid)
    void driveToTile(int tileX, int tileY, int heading);

    // Intake a specific row (0 == human intake, 1..3 rows)
    void intakeRow(int row);

    // Deposit by mode ("near" or "far") with sorted flag
    void depositMode(String mode, boolean sorted);

    // Deposit at a specific tile coordinate with heading
    void depositTile(int tileX, int tileY, int heading, boolean sorted);

    // Release gate action
    void releaseGate();

    // Delay/blocking call (milliseconds)
    void delayMs(int ms);

    // Optional custom action example (e.g. launch shooter)
    void launch();

    /**
     * Optional hook: block until the previous action completes. Implementations
     * can make methods above non-blocking and use this to wait. Default no-op.
     */
    default void waitForCompletion() {
        // Default: do nothing. Override to block until subsystem idle.
    }
}