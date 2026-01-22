package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.commands.Commands.drive;
import static org.firstinspires.ftc.teamcode.commands.Commands.wait;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.Axial.BACK;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.Axial.FRONT;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.Lateral.LEFT;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.Lateral.RIGHT;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static java.lang.Math.abs;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;
import org.firstinspires.ftc.teamcode.game.Side;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class QuanomousCommands {
    private final Map<String, Function<JSONObject, Command>> commands =
        new HashMap<String, Function<JSONObject, Command>>() {{
            put("delay", Lambda.unchecked(QuanomousCommands::delay));
            put("intake", Lambda.unchecked(QuanomousCommands::intake));
            put("deposit", Lambda.unchecked(QuanomousCommands::deposit));
            put("release", Lambda.unchecked(QuanomousCommands::release));
            put("drive", Lambda.unchecked(QuanomousCommands::drive));
        }};

    public static Command delay(JSONObject obj) throws Exception {
        return wait.seconds(
            obj.getDouble("seconds")
        );
    }

    public static Command intake(JSONObject obj) throws Exception {
        int spike = obj.getInt("spike");
        return auto.intake(spike);
    }

    public static Command deposit(JSONObject obj) throws Exception {
        String locale = obj.getString("locale");
        Side side = locale.equals("near") ? SOUTH : NORTH;
        double txo = obj.getDouble("txo") * TILE_WIDTH;
        double tyo = obj.getDouble("tyo") * TILE_WIDTH;
        return auto.deposit(side, txo, tyo);
    }

    public static Command release(JSONObject obj) {
        return auto.releaseGate();
    }

    public static Command drive(JSONObject obj) throws Exception {
        String axial = obj.optString("axial", "center").toLowerCase();
        String lateral = obj.optString("lateral", "center").toLowerCase();
        return drive.curve(
            nav.createPose(
                obj.getDouble("tx") * TILE_WIDTH,
                abs(obj.getDouble("ty")) * -config.alliance.sign * TILE_WIDTH,
                obj.getDouble("h"),
                (axial.equals("front") ? FRONT : (axial.equals("back") ? BACK : NavSubsystem.Axial.CENTER)),
                (lateral.equals("left") ? LEFT : (lateral.equals("right") ? RIGHT : NavSubsystem.Lateral.CENTER))
            )
        );
    }

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
