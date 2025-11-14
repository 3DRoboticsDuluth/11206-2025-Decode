package org.firstinspires.ftc.teamcode.adaptations.ballistics;

import static java.lang.Math.max;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.FFCoefficients;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.PIDFController;

public class FlywheelController {
    private SimpleMotorFeedforward ff;
    private PIDFController pidf = new PIDFController(new PIDFCoefficients());
    private double target;

    public FlywheelController(FFCoefficients ff, PIDFCoefficients pidf) {
        this.setCoefficients(ff, pidf);
    }

    /** @noinspection DataFlowIssue*/
    public void setCoefficients(FFCoefficients ff, PIDFCoefficients pidf) {
        this.ff = new SimpleMotorFeedforward(ff.ks, ff.kv, ff.ka);
        this.pidf.setPIDFCoefficients(pidf);
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double update(double actual, double voltage) {
        double ffPower = this.ff.calculate(target);
        double pidfPower = this.pidf.calculate(actual, target);
        double totalPower = ffPower + pidfPower;
        return totalPower * (12 / max(1, voltage));
    }
}
