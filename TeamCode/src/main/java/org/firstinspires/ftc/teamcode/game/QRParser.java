package org.firstinspires.ftc.teamcode.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * QRParser
 *
 * Parses a decoded QR plan from QRDecoder and executes it on a RobotActions implementation.
 * Supports all RobotActions methods, including drive, intake, deposit, release gate, delay, and launch.
 */
public class QRParser {

    private final RobotActions robotActions;

    public QRParser(RobotActions robotActions) {
        this.robotActions = robotActions;
    }

    /**
     * Execute a full plan decoded from a QR code.
     * Each element in the plan array can be a JSONObject or a JSON string representing a command.
     */
    public void executePlan(JSONArray plan) {
        for (int i = 0; i < plan.length(); i++) {
            try {
                Object item = plan.get(i);
                JSONObject cmdObj;

                // Convert string commands to JSONObjects
                if (item instanceof JSONObject) {
                    cmdObj = (JSONObject) item;
                } else if (item instanceof String) {
                    cmdObj = new JSONObject((String) item);
                } else {
                    System.out.println("Skipping unknown command format: " + item);
                    continue;
                }

                executeCommand(cmdObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Execute a single command object by mapping the "cmd" key to the appropriate RobotActions method.
     */
    private void executeCommand(JSONObject cmdObj) throws JSONException {
        String cmd = cmdObj.getString("cmd");

        switch (cmd) {
            case "intake_row":
                int row = cmdObj.getInt("row");
                robotActions.intakeRow(row);
                robotActions.waitForCompletion();
                break;

            case "delay_ms":
                int ms = cmdObj.getInt("ms");
                robotActions.delayMs(ms);
                break;

            case "deposit":
                String mode = cmdObj.getString("mode"); // "near" or "far"
                boolean sorted = cmdObj.getBoolean("sorted");
                robotActions.depositMode(mode, sorted);
                robotActions.waitForCompletion();
                break;

            case "drive_to_coords":
                double x = cmdObj.getDouble("x");
                double y = cmdObj.getDouble("y");
                int heading = cmdObj.getInt("heading");
                robotActions.driveToCoords(x, y, heading);
                robotActions.waitForCompletion();
                break;

            case "drive_to_tile":
                int tileX = cmdObj.getInt("tileX");
                int tileY = cmdObj.getInt("tileY");
                heading = cmdObj.getInt("heading");
                robotActions.driveToTile(tileX, tileY, heading);
                robotActions.waitForCompletion();
                break;

            case "deposit_tile":
                tileX = cmdObj.getInt("tileX");
                tileY = cmdObj.getInt("tileY");
                heading = cmdObj.getInt("heading");
                sorted = cmdObj.getBoolean("sorted");
                robotActions.depositTile(tileX, tileY, heading, sorted);
                robotActions.waitForCompletion();
                break;

            case "release_gate":
                robotActions.releaseGate();
                robotActions.waitForCompletion();
                break;

            case "launch":
                robotActions.launch();
                robotActions.waitForCompletion();
                break;

            default:
                System.out.println("Unknown cmd: " + cmd);
        }
    }
}
