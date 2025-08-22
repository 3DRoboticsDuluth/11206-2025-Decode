package org.firstinspires.ftc.teamcode.game;

import static java.lang.Double.NaN;

public enum Side {
    UNKNOWN(NaN),
    NORTH(+1),
    SOUTH(-1);

    public final double sign;

    Side(double sign) {
        this.sign = sign;
    }
}
