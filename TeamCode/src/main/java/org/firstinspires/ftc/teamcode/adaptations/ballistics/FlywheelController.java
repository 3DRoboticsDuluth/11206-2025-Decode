package org.firstinspires.ftc.teamcode.adaptations.ballistics;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFCoefficients;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.PIDFController;

public class FlywheelController {
    private SimpleMotorFeedforward ff;
    private final PIDFController pidf = new PIDFController(new PIDFCoefficients());
    private double target;

    public FlywheelController(FFCoefficients ff, PIDFCoefficients pidf) {
        this.setCoefficients(ff, pidf);
    }

    public void setCoefficients(FFCoefficients ff, PIDFCoefficients pidf) {
        this.ff = new SimpleMotorFeedforward(ff.ks, ff.kv, ff.ka);
        this.pidf.setPIDFCoefficients(pidf);
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double update(double actual) {
        double ffPower = this.ff.calculate(target);
        double pidfPower = this.pidf.calculate(actual, target);
        return ffPower + pidfPower;
    }
}
