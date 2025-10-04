package org.firstinspires.ftc.teamcode.game;

import org.firstinspires.ftc.teamcode.adaptations.pedropathing.CurveOptimization;

import java.util.ArrayList;
import java.util.List;

public class FieldZones {
    public static void initAllZones() {
        CurveOptimization.clearNoGoZones();

        initFieldWalls();
        initGoalZones();
    }
    public static void initFieldWalls() {
        CurveOptimization.clearNoGoZones();

        double halfField = 72;
        double wallThickness = 1;

        // North wall
        addNoGoZone(0, ZoneType.HARD,
                -halfField, halfField,
                halfField, halfField,
                halfField, halfField - wallThickness,
                -halfField, halfField - wallThickness
        );

        // South wall
        addNoGoZone(0, ZoneType.HARD,
                -halfField, -halfField + wallThickness,
                halfField, -halfField + wallThickness,
                halfField, -halfField,
                -halfField, -halfField
        );

        // West wall
        addNoGoZone(0, ZoneType.HARD,
                -halfField, -halfField + wallThickness,
                -halfField + wallThickness, -halfField + wallThickness,
                -halfField + wallThickness, halfField - wallThickness,
                -halfField, halfField - wallThickness
        );

        // East wall (right edge)
        addNoGoZone(0, ZoneType.HARD,
                halfField - wallThickness, -halfField + wallThickness,
                halfField, -halfField + wallThickness,
                halfField, halfField - wallThickness,
                halfField - wallThickness, halfField - wallThickness
        );
    }


    public static void addNoGoZone(double penalty, ZoneType type, double... coords) {
        if (coords.length % 2 != 0) {
            throw new IllegalArgumentException("Coordinates must come in x,y pairs");
        }

        List<double[]> points = new ArrayList<>();
        for (int i = 0; i < coords.length; i += 2) {
            points.add(new double[]{coords[i], coords[i+1]});
        }

        NoGoZone zone = new NoGoZone(points, penalty, type);
        CurveOptimization.addNoGoZone(zone);
    }

    public static void initGoalZones() {
        addNoGoZone(0, ZoneType.HARD, //Blue Goal Zone
                72, -72,
                -1, -72,
                -1, -64.5,
                49.6, -64.5,
                72, -48.5
        );

        addNoGoZone(0, ZoneType.HARD, //Red Goal Zone
                72, 72,
                -1, 72,
                -1, 64.5,
                49.6, -64.5,
                72, -48.5
        );
    }

}
