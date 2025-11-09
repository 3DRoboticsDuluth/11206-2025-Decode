package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
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

    //    {"cmd":"drive","tx":2,"ty":2,"h":180},
    //    {"cmd":"drive","x":0,"y":0,"h":0},
    //    {"cmd":"intake","spike":2},
    //    {"cmd":"delay","seconds":1},
    //    {"cmd":"release"}
    //    {"cmd":"deposit","locale":"near","sorted":false,"txo":2,"tyo":2},

    private final Map<String, Function<JSONObject, Command>> commands =
        new HashMap<String, Function<JSONObject, Command>>() {{
            put("drive_to", Lambda.unchecked(QuanomousCommands::drive));
            put("drive_to", Lambda.unchecked(QuanomousCommands::drive));
        }};

    // TODO: Change `drive_to` to `drive`.
    public static Command drive(JSONObject obj) throws Exception {
        return drive.to(
            obj.getDouble("x"),
            obj.getDouble("y"),
            obj.getDouble("h")
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
