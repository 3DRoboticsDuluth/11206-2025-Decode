package org.firstinspires.ftc.teamcode.adaptations.solverslib;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class PIDFController extends com.seattlesolvers.solverslib.controller.PIDFController {
    public PIDFController(PIDFCoefficients coefficients) {
        super(coefficients);
    }

    public void setPIDFCoefficients(PIDFCoefficients coefficients) {
        this.setPIDF(
            coefficients.p,
            coefficients.i,
            coefficients.d,
            coefficients.f
        );
    }
}
