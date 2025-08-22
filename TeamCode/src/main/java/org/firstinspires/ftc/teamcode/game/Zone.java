package org.firstinspires.ftc.teamcode.game;

public enum Zone {
    OUTER(-1),
    INNER(+1);

    public final int sign;

    Zone(int sign) {
        this.sign = sign;
    }
}
