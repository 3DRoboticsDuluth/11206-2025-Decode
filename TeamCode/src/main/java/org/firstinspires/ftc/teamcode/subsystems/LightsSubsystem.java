package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.adaptations.gobilda.Color.BLUE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.Color.RED;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.GoBildaPrismDriver.LayerHeight.LAYER_0;
import static org.firstinspires.ftc.teamcode.game.Config.config;

import org.firstinspires.ftc.teamcode.adaptations.gobilda.Color;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.PrismAnimations;
import org.firstinspires.ftc.teamcode.game.Alliance;

public class LightsSubsystem extends HardwareSubsystem {
    public GoBildaPrismDriver prism;

    public LightsSubsystem() {
        prism = getDevice(GoBildaPrismDriver.class, "prism", p -> p.setStripLength(48));
    }

    @Override
    public void periodic() {
        if (unready()) return;

        if(config.auto) {
            if (config.alliance == Alliance.RED) set(RED);
            else if (config.alliance == Alliance.BLUE) set(BLUE);
        }
    }

    public void set(Color color) {
        prism.insertAndUpdateAnimation(
            LAYER_0, new PrismAnimations.Solid(color)
        );
    }
}
