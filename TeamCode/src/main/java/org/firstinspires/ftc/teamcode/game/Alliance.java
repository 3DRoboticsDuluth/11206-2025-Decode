package org.firstinspires.ftc.teamcode.game;

import static java.lang.Double.NaN;

public enum Alliance {
    UNKNOWN(NaN),
    BLUE(+1),
    RED(-1);

    public final double sign;

    Alliance(double sign) {
        this.sign = sign;
    }
}
