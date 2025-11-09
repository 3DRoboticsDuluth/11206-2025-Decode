package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.intake;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class QuanomousCommands {
    // H4sIAAAAAAAAA32PwQoCMQxE/yXnHrrX/opICXZci91W2qAu4r9vdkUoi3gZJpk3gRxedJoCOQo13uGlkKEnOWto3vQCDjGP6t/mi8YsfIWv5aHwqm74F3bNgMSzn5pGKm6wtgsrErjBjyygvnMrLcpaKQG6yOCqUytVoMCZU8MvXGKC//yy2f1D+xPHBcupcmIMAQAA

    // [
    //   { "cmd": "drive", "tx": 0, "ty": 0, "h": 0 },
    //   { "cmd": "intake", "spike": 1 },
    //   { "cmd": "delay", "seconds": 1000 },
    //   { "cmd": "release" },
    //   { "cmd": "deposit", "locale": "near", "sorted": false, "txo": 0, "tyo": 0}

    private final Map<String, Function<JSONObject, Command>> commands =
        new HashMap<String, Function<JSONObject, Command>>() {{
            put("drive", Lambda.unchecked(QuanomousCommands::drive));
            put("intake", Lambda.unchecked(QuanomousCommands::intake));
            put("delay", Lambda.unchecked(QuanomousCommands::delay));
            put("release", Lambda.unchecked(QuanomousCommands::release));
            put("deposit", Lambda.unchecked(QuanomousCommands::deposit));
        }};

    // TODO: Change `drive_to` to `drive`.
    public static Command drive(JSONObject obj) throws Exception {
        return drive.to(
            obj.getDouble("x"),
            obj.getDouble("y"),
            obj.getDouble("h")
        );
    }

    public static Command intake(JSONObject obj) throws Exception {
        return drive.toSpike0(); //
    }

    public static Command delay(JSONObject obj) throws Exception {
        return wait.seconds(
                obj.getDouble("seconds")
        );
    }

    public static Command release(JSONObject obj) throws Exception {
        return drive.toGate();
    }

    public static Command deposit(JSONObject obj) throws Exception {
        return drive.toLaunchFar(
//                obj.getString("locale"),
//                obj.getString("sorted"),
//                obj.getDouble("txo"),
//                obj.getDouble("tyo")
        );
    }

    // TODO: Change `intake_row` to `intake`.
    /** @noinspection DataFlowIssue*/
    public Command execute() {
        try {
            SequentialCommandGroup group = new SequentialCommandGroup();

            JSONArray jsonArray = Quanomous.load(config.quanomous);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String cmd = jsonObject.getString("cmd");
                group.addCommands(commands.get(cmd).apply(jsonObject));
            }

            return group;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface Lambda<T, R> {
        R apply(T t) throws Exception;

        static <T, R> Function<T, R> unchecked(Lambda<T, R> f) {
            return t -> {
                try {
                    return f.apply(t);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }
}
