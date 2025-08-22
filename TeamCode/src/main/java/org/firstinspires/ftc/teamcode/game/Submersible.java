package org.firstinspires.ftc.teamcode.game;

import static org.firstinspires.ftc.teamcode.game.Config.config;

import java.util.Arrays;

public enum Submersible {
    NE(-30),
    E(-90),
    SE(-150),
    SW(150),
    W(90),
    NW(30);

    private static final String legend = "NESW";

    private static final String[][] characters = {
        {" ###  ## ", " ####### ", " ####### ", " ##   ## "},
        {" #### ## ", " ##      ", " ##      ", " ##   ## "},
        {" ## #### ", " #####   ", " ####### ", " ## # ## "},
        {" ##  ### ", " ##      ", "      ## ", " ## # ## "},
        {" ##   ## ", " ####### ", " ####### ", "  ## ##  "}
    };
    
    public final double angle;
    
    Submersible(double angle) {
        this.angle = angle;
    }

    public static Submersible getFromAngle(double angle) {
        return Arrays.stream(Submersible.values())
            .filter(sub -> Math.abs(angle - sub.angle) <= 30)
            .findFirst().orElse(NW);
    }

    public static String[] getDisplayLines() {
        String[] lines = new String[5];
        if (config.submersible == null) return lines;
        String name = config.submersible.name();
        Arrays.fill(lines, "");

        for (int i = 0; i < characters.length; i++) {
            for (int j = 0; j < name.length(); j++) {
                char character = name.charAt(j);
                int index = legend.indexOf(character);
                lines[i] += characters[i][index];
            }
        }

        return lines;
    }
}
