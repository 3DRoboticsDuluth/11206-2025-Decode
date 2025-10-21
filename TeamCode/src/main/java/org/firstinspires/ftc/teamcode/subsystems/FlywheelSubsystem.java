package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.BARE;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class FlywheelSubsystem extends HardwareSubsystem {
    public static boolean TELEM = false;
    public static double VEL = 0;
    public static double KS = 0;
    public static double KV = 1;
    public static double KA = 0;

    public MotorEx motorLeft;
    public MotorEx motorRight;

    public FlywheelSubsystem() {
        motorLeft = getMotor("flywheelLeft", BARE, m -> configure(m, true));
        motorRight = getMotor("flywheelRight", BARE, m -> configure(m, false));
    }

    @Override
    public void periodic() {
        if (hasErrors()) return;
        set(motorLeft, "Flywheel Left");
        set(motorRight, "Flywheel Right");
    }

    public void start() {
        VEL = 0.5;
    }

    public void stop() {
        VEL = 0;
    }

    private void configure(MotorEx motor, boolean inverted) {
        motor.setInverted(inverted);
        motor.stopAndResetEncoder();
        motor.setRunMode(VelocityControl);
    }

    private void set(MotorEx motor, String name) {
        motor.setFeedforwardCoefficients(KS, KV, KA);
        motor.setVelocityPercentage(VEL);
        motor.addTelemetry(name, TELEM);
    }
}
