package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.RED;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver.Artboard.ARTBOARD_0;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver.LayerHeight.LAYER_0;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import android.annotation.SuppressLint;

import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.PrismAnimations;

public class LightsSubsystem extends HardwareSubsystem {
    public GoBildaPrismDriver prism;

    public LightsSubsystem() {
        prism = getDevice(GoBildaPrismDriver.class, "prism", this::configure);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;
        telemetry.addData("Lights", () -> String.format("%d leds %d fps", prism.getNumberOfLEDs(), prism.getCurrentFPS()));
    }

    public void set(Color color) {
        prism.insertAndUpdateAnimation(
            LAYER_0, new PrismAnimations.Solid(color)
        );
    }

    private void configure(GoBildaPrismDriver prism) {
        prism.setStripLength(36);
        prism.insertAndUpdateAnimation(LAYER_0, new PrismAnimations.Solid(RED));
        prism.saveCurrentAnimationsToArtboard(ARTBOARD_0);
        prism.setDefaultBootArtboard(ARTBOARD_0);
        prism.enableDefaultBootArtboard(true);
    }
}
