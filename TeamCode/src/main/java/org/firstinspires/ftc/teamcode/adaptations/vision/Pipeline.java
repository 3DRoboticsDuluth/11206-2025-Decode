package org.firstinspires.ftc.teamcode.adaptations.vision;

public enum Pipeline {
    QRCODE(0),
    APRILTAG(1),
    COLOR(2);

    public final int index;

    Pipeline(int index) {
        this.index = index;
    }
}
