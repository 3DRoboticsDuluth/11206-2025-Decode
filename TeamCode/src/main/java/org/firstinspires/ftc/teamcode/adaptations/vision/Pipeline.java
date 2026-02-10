package org.firstinspires.ftc.teamcode.adaptations.vision;

public enum Pipeline {
    QRCODE(0),
    APRILTAG(1),
    GREEN(2),
    PURPLE(3),
    PURPLE_LEFT(4),
    PURPLE_RIGHT(5);

    public final int index;

    Pipeline(int index) {
        this.index = index;
    }
}
