package org.firstinspires.ftc.teamcode.game;

import static org.firstinspires.ftc.teamcode.game.Pipeline.BLUE_SAMPLE;
import static org.firstinspires.ftc.teamcode.game.Pipeline.BLUE_SPECIMEN;
import static org.firstinspires.ftc.teamcode.game.Pipeline.RED_SAMPLE;
import static org.firstinspires.ftc.teamcode.game.Pipeline.RED_SPECIMEN;
import static java.lang.Double.NaN;

public enum Alliance {
    UNKNOWN(NaN, null, null),
    BLUE(+1, BLUE_SAMPLE, BLUE_SPECIMEN),
    RED(-1, RED_SAMPLE, RED_SPECIMEN);

    public final double sign;

    public final Pipeline samplePipeline;
    public final Pipeline specimenPipeline;

    Alliance(double sign, Pipeline samplePipeline, Pipeline specimenPipeline) {
        this.sign = sign;
        this.samplePipeline = samplePipeline;
        this.specimenPipeline = specimenPipeline;
    }
}
