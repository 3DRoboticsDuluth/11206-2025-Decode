package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;

import org.firstinspires.ftc.teamcode.adaptations.gobilda.Color;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.PrismAnimations;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.firstinspires.ftc.teamcode.game.Alliance;

public class LightSubsystem extends HardwareSubsystem {
    public Servo leftLight;
    public Servo rightLight;

    public GoBildaPrismDriver prism;

    public LightSubsystem() {
        leftLight = getServo("leftLight");
        rightLight = getServo("rightLight");
        prism = getDevice(GoBildaPrismDriver.class, "prism");

        /*
         * Set the number of LEDs (starting at 0) that are in your strip. This can be longer
         * than the actual length of the strip, but some animations won't look quite right.
         */
        prism.setStripLength(32);
    }

    public static double LEFT_LIGHT = 0;
    public static double RIGHT_LIGHT = 0;

    public static double RED = 0.277;
    public static double ORANGE = 0.333;
    public static double YELLOW = 0.388;
    public static double GREEN = 0.500;
    public static double BLUE = 0.611;

    @Override
    public void periodic() {
        if (unready()) return;

        if(config.auto) {
            if (config.alliance == Alliance.RED) {
                LEFT_LIGHT = RED;
                RIGHT_LIGHT = RED;
            }
            else if (config.alliance == Alliance.BLUE) {
                LEFT_LIGHT = BLUE;
                RIGHT_LIGHT = BLUE;
            }
        }

        leftLight.setPosition(LEFT_LIGHT);
        rightLight.setPosition(RIGHT_LIGHT);
    }

    public void setRed() {
        PrismAnimations.Solid red = new PrismAnimations.Solid(Color.RED);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, red);
    }

    public void setOrange() {
        PrismAnimations.Solid orange = new PrismAnimations.Solid(Color.ORANGE);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, orange);
    }
    public void setYellow() {
        PrismAnimations.Solid yellow = new PrismAnimations.Solid(Color.YELLOW);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, yellow);
    }
    public void setGreen() {
        PrismAnimations.Solid green = new PrismAnimations.Solid(Color.GREEN);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, green);
    }
    public void zero() {
        LEFT_LIGHT = RED;
        RIGHT_LIGHT = RED;
        setRed();
    }

    public void one() {
        LEFT_LIGHT = ORANGE;
        RIGHT_LIGHT = ORANGE;
        setOrange();
    }

    public void two() {
        LEFT_LIGHT = YELLOW;
        RIGHT_LIGHT = YELLOW;
        setYellow();
    }

    public void three() {
        LEFT_LIGHT = GREEN;
        RIGHT_LIGHT = GREEN;
        setGreen();
    }
}
