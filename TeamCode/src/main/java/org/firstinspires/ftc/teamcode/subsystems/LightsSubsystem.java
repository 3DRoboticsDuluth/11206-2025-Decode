package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.BLUE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.GREEN;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.ORANGE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.RED;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.WHITE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver.Artboard.ARTBOARD_0;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver.LayerHeight.LAYER_0;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.intake;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.playTimer;

import android.annotation.SuppressLint;

import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.PrismAnimations;
import org.firstinspires.ftc.teamcode.game.Alliance;

public class LightsSubsystem extends HardwareSubsystem {
    public GoBildaPrismDriver prism;

    public Color color = RED;

    public LightsSubsystem() {
        prism = getDevice(GoBildaPrismDriver.class, "prism", this::configure);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

//        if (config.started && config.teleop && playTimer.seconds() > 110) set(RED);
//        else if (config.started && config.teleop && playTimer.seconds() > 100) set(ORANGE);
//        else if (config.started && config.teleop && playTimer.seconds() > 80) set(WHITE);
        /*else*/ if (intake.full) set(GREEN);
        else if (config.alliance == Alliance.RED) set(RED);
        else if (config.alliance == Alliance.BLUE) set(BLUE);

        telemetry.addData("Lights", () -> String.format("%d leds %d fps", prism.getNumberOfLEDs(), prism.getCurrentFPS()));
    }

    public void set(Color color) {
        if (this.color == color) return;
        prism.insertAndUpdateAnimation(
            LAYER_0, new PrismAnimations.Solid(this.color = color)
        );
    }

    private void configure(GoBildaPrismDriver prism) {
        prism.setStripLength(48);
        prism.insertAndUpdateAnimation(LAYER_0, new PrismAnimations.Solid(RED));
        prism.saveCurrentAnimationsToArtboard(ARTBOARD_0);
        prism.setDefaultBootArtboard(ARTBOARD_0);
        prism.enableDefaultBootArtboard(true);
    }
}
