package org.firstinspires.ftc.teamcode.game;

import static org.firstinspires.ftc.teamcode.game.Element.SAMPLE;
import static org.firstinspires.ftc.teamcode.game.Element.SPECIMEN;
import static org.firstinspires.ftc.teamcode.game.Position.ENTER;
import static org.firstinspires.ftc.teamcode.game.Position.EXIT;
import static org.firstinspires.ftc.teamcode.game.Position.TARGET;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static java.lang.Math.toRadians;

import java.util.HashMap;

public enum Location {
    BASKET_DEPOSIT(SAMPLE, false, new Pose(-TILE_WIDTH * 0.33, 0, 0), new Pose(-TILE_WIDTH * 0.5, 0, 0)),
    FIELD_INTAKE(SAMPLE, false, new Pose(0, 0, 0), new Pose(0, 0, 0)),
    OBSERVATION_SAMPLE(SAMPLE, false, new Pose(-TILE_WIDTH * 0, 0, 0), new Pose(-TILE_WIDTH * 0, 0, 0)),
    OBSERVATION_SPECIMEN(SPECIMEN, true, new Pose(-TILE_WIDTH * 0.33, 0, 0), new Pose(-TILE_WIDTH * 0.2, 0, toRadians(-45))),
    OBSERVATION_PARK(null, false, new Pose(0, 0, 0), new Pose(0, 0, 0)),
    SPIKE_INTAKE(SAMPLE, false, new Pose(-TILE_WIDTH * 0.2, 0, 0), new Pose(-TILE_WIDTH * 0, 0, 0)),
    SUBMERSIBLE_INTAKE(SAMPLE, false, new Pose(-TILE_WIDTH * 0.75, 0, 0), new Pose(-TILE_WIDTH * 0.75, 0, 0)),
    SUBMERSIBLE_DEPOSIT(SPECIMEN, false, new Pose(-TILE_WIDTH * 0.33, 0, 0), new Pose(-TILE_WIDTH * 0.25, TILE_WIDTH * 0.25, toRadians(45))),
    SUBMERSIBLE_PARK(null, false, new Pose(TILE_WIDTH, 0, 0), new Pose(0, 0, 0)),
    SUBMERSIBLE_ASCENT(null, false, new Pose(TILE_WIDTH * 0.75, 0, 0), new Pose(0, 0, 0));

    public final Element element;
    public final boolean precise;
    public final HashMap<Position, Pose> offsets = new HashMap<>();
    
    Location(Element element, boolean precise, Pose enterOffset, Pose exitOffset) {
        this.element = element;
        this.precise = precise;
        offsets.put(ENTER, enterOffset);
        offsets.put(TARGET, new Pose(0, 0, 0));
        offsets.put(EXIT, exitOffset);
    }
}
