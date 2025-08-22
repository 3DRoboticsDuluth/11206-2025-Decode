package org.firstinspires.ftc.teamcode.game;

public enum Pipeline {
    APRILTAG(0),
    RED_SAMPLE(1),
    BLUE_SAMPLE(2),
    YELLOW(3),
    RED_SPECIMEN(5),
    BLUE_SPECIMEN(6);
    
    public final int index;

    Pipeline(int index) {
        this.index = index;
    }
}
