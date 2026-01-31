package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_RED;
import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.STROBE_RED;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.BLUE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.ORANGE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.RED;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color.WHITE;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver.Artboard.ARTBOARD_0;
import static org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver.LayerHeight.LAYER_0;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.timing;
import static org.firstinspires.ftc.teamcode.subsystems.TimingSubsystem.playTimer;

import android.annotation.SuppressLint;

import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.Color;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.prism.PrismAnimations;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;

public class LightsSubsystem extends HardwareSubsystem {
    public GoBildaPrismDriver prism;

    public Color color;

    public LightsSubsystem() {
        prism = getDevice(GoBildaPrismDriver.class, "prism", this::configure);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void periodic() {
        if (unready()) return;

        if (!config.started) {
            if (config.alliance == Alliance.RED) set(RED);
            else if (config.alliance == Alliance.BLUE) set(BLUE);
        } else if (!config.auto) {
            if (playTimer.seconds() >= 105 && playTimer.seconds() <= 129) set(WHITE);
            else if (playTimer.seconds() >= 130 && playTimer.seconds() <= 142) set(ORANGE);
            else if (playTimer.seconds() >= 143) set(RED);
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
        prism.setStripLength(36);
        prism.insertAndUpdateAnimation(LAYER_0, new PrismAnimations.Solid(RED));
        prism.saveCurrentAnimationsToArtboard(ARTBOARD_0);
        prism.setDefaultBootArtboard(ARTBOARD_0);
        prism.enableDefaultBootArtboard(true);
    }
}
