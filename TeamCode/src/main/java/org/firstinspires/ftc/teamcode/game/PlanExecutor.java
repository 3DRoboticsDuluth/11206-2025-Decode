package org.firstinspires.ftc.teamcode.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * PlanExecutor
 *
 * Given the JSONArray output from QRDecoder.decodePlan, call methods on
 * a RobotActions implementation to perform each step.
 */
public class PlanExecutor {

    private final RobotActions actions;

    public PlanExecutor(RobotActions actions) {
        this.actions = actions;
    }

    public void execute(JSONArray plan) throws JSONException {
        for (int i = 0; i < plan.length(); i++) {
            Object o = plan.get(i);
            if (o instanceof JSONObject) {
                executeCommand((JSONObject) o);
            } else if (o instanceof String) {
                // If inner strings exist (older format) try to parse
                try {
                    JSONObject obj = new JSONObject((String) o);
                    executeCommand(obj);
                } catch (JSONException e) {
                    // ignore unknown string
                }
            }
        }
    }

    private void executeCommand(JSONObject obj) throws JSONException {
        String cmd = obj.optString("cmd", "");
        switch (cmd) {
            case "drive_to":
                if (obj.has("use_tiles") || obj.has("tile_x") || obj.has("tile_y")) {
                    int tx = obj.optInt("tile_x", 0);
                    int ty = obj.optInt("tile_y", 0);
                    int heading = obj.optInt("heading", 0);
                    actions.driveToTile(tx, ty, heading);
                } else {
                    double x = obj.optDouble("x", 0.0);
                    double y = obj.optDouble("y", 0.0);
                    int heading = obj.optInt("heading", 0);
                    actions.driveToCoords(x, y, heading);
                }
                break;
            case "intake_row":
                int row = obj.optInt("row", 0);
                actions.intakeRow(row);
                break;
            case "delay_ms":
                int ms = obj.optInt("ms", 0);
                actions.delayMs(ms);
                break;
            case "deposit":
                if (obj.has("tile_x") || obj.has("tile_y")) {
                    int tx = obj.optInt("tile_x", 0);
                    int ty = obj.optInt("tile_y", 0);
                    int heading = obj.optInt("heading", 0);
                    boolean sorted = obj.optBoolean("sorted", false);
                    actions.depositTile(tx, ty, heading, sorted);
                } else {
                    String mode = obj.optString("mode", "near");
                    boolean sorted = obj.optBoolean("sorted", false);
                    actions.depositMode(mode, sorted);
                }
                break;
            case "release_gate":
                actions.releaseGate();
                break;
            case "launch":
                actions.launch();
                break;
            default:
                // Unknown command: ignore or log
                break;
        }

        // Optional synchronization point after each command
        actions.waitForCompletion();
    }
}