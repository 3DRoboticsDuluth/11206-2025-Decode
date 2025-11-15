package org.firstinspires.ftc.teamcode.adaptations.solverslib;

/** @noinspection unused*/
public class FFController {
    public FFCoefficients coefficients;

    public FFController(FFCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    public double calculate(double velocity, double acceleration) {
        return coefficients.ks * Math.signum(velocity) +
            coefficients.kv * velocity +
            coefficients.ka * acceleration;
    }

    public double calculate(double velocity) {
        return calculate(velocity, 0);
    }
}
