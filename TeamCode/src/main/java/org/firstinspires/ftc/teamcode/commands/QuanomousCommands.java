package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SelectCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class QuanomousCommands {
    private final Map<String, Function<JSONObject, Command>> commands =
        new HashMap<String, Function<JSONObject, Command>>() {{
            put("drive", Lambda.unchecked(QuanomousCommands::drive));
            put("intake", Lambda.unchecked(QuanomousCommands::intake));
            put("delay", Lambda.unchecked(QuanomousCommands::delay));
            put("release", Lambda.unchecked(QuanomousCommands::release));
            put("deposit", Lambda.unchecked(QuanomousCommands::deposit));
        }};

    public static Command drive(JSONObject obj) throws Exception {
        return drive.to(
            obj.getDouble("tx") * TILE_WIDTH,
            obj.getDouble("ty") * TILE_WIDTH,
            obj.getDouble("h")
        );
    }

    public static Command intake(JSONObject obj) throws Exception {
        return auto.intakeSpike(
            obj.getInt("spike")
        );
    }

    public static Command delay(JSONObject obj) throws Exception {
        return wait.seconds(
            obj.getDouble("seconds")
        );
    }

    public static Command release(JSONObject obj) throws Exception {
        return auto.releaseGate();
    }

    public static Command deposit(JSONObject obj) throws Exception {
        String locale = obj.getString("locale");
        return new SelectCommand(
            new HashMap<Object, Command>() {{
                put("near", auto.depositNear());
                put("far", auto.depositFar());
            }}, () -> locale
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
