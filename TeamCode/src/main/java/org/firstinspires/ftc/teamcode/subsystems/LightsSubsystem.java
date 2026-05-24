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
import android.hardware.camera2.params.BlackLevelPattern;

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

        if (intake.robotIsFull) {
            set(GREEN);
        }
        else if (playTimer.seconds() < 80 && config.alliance == Alliance.RED) set(RED);
        else if (playTimer.seconds() < 80 && config.alliance == Alliance.BLUE) set(BLUE);

        if (!config.started) {
            if (config.alliance == Alliance.RED) set(RED);
            else if (config.alliance == Alliance.BLUE) set(BLUE);
        } else if (!config.auto) {
            if (playTimer.seconds() > 110) set(RED);
            else if (playTimer.seconds() > 100) set(ORANGE);
            else if (playTimer.seconds() > 80) set(WHITE);
        }

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
