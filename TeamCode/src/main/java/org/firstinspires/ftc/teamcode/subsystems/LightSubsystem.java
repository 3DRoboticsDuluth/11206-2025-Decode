package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.BLUE;
import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_BLUE;
import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_RED;
import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.LIGHT_CHASE_BLUE;
import static com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern.LIGHT_CHASE_RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;

import org.firstinspires.ftc.teamcode.adaptations.gobilda.Color;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.adaptations.gobilda.PrismAnimations;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;
import org.opencv.dnn.Layer;

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
        PrismAnimations.Solid red = new PrismAnimations.Solid(Color.RED);
        PrismAnimations.Solid orange = new PrismAnimations.Solid(Color.ORANGE);
        PrismAnimations.Solid yellow = new PrismAnimations.Solid(Color.YELLOW);
        PrismAnimations.Solid green = new PrismAnimations.Solid(Color.GREEN);
        PrismAnimations.Solid solid = new PrismAnimations.Solid(Color.BLUE);
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

    public void zero() {
        LEFT_LIGHT = RED;
        RIGHT_LIGHT = RED;

    }

    public void one() {
        LEFT_LIGHT = ORANGE;
        RIGHT_LIGHT = ORANGE;
    }

    public void two() {
        LEFT_LIGHT = YELLOW;
        RIGHT_LIGHT = YELLOW;
    }

    public void three() {
        LEFT_LIGHT = GREEN;
        RIGHT_LIGHT = GREEN;
    }
}
