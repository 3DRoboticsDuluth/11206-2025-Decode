package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.robotcore.hardware.DigitalChannel.Mode.INPUT;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.RunMode.VelocityControl;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.ZeroPowerBehavior.FLOAT;

import static org.firstinspires.ftc.teamcode.commands.Commands.lights;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.adaptations.solverslib.MotorEx;

@Configurable
public class IntakeSubsystem extends HardwareSubsystem {
    public static final double STOP = 0;
    public static double HOLD = 0.50;
    public static double FWD = 0.75;
    public static double REV = -0.25;
    public static double VEL = STOP;
    public static double TIMER_THRESHOLD = 0.1;
    public static boolean TEL = false;

    public MotorEx motor;
    public DigitalChannel laser;
    public DigitalChannel laser2;
    public boolean stateLast = false;
    public boolean stateNext = false;
    public boolean stateEnabling = false;
    public boolean stateDisabling = false;
    public int count = 0;
    public boolean artifactDetected = false;
    public boolean robotIsFull = false;
    public double artifactsInBotPrev = 0;
    public double maxArtifacts = 3;
    public double artifactsInBot = Math.min(artifactsInBotPrev, maxArtifacts);
    public boolean currentState1 = false;
    public boolean previousState1 = false;
    public boolean currentState2 = false;
    public boolean previousState2 = false;
    public ElapsedTime timer = new ElapsedTime();

    public IntakeSubsystem() {
        motor = getMotor("intake", RPM_1150, this::configure);
        laser = getDevice(DigitalChannel.class, "laser", l -> l.setMode(INPUT));
        laser2 = getDevice(DigitalChannel.class, "laser2", l -> l.setMode(INPUT));
        VEL = STOP;
    }

    @Override
    public void periodic() {
        if (unready()) return;

        motor.setVelocityPercentage(VEL);

        previousState1 = currentState1;
        currentState1 = laser.getState();

        previousState2 = currentState2;
        currentState2 = laser2.getState();

        stateLast = stateNext;
        stateNext = laser.getState();
        stateEnabling = !stateLast && stateNext;
        stateDisabling = stateLast && !stateNext;
        if (stateDisabling) timer.reset();
        else if (stateEnabling && timer.seconds() > TIMER_THRESHOLD) count++;

        detectArtifact();
        artifactCount();

        motor.addTelemetry(TEL);

        telemetry.addData("Intake (ArtisInBot)", () -> String.format("%.1f", artifactsInBot));
        telemetry.addData("Intake (Laser)", () -> laser.getState() ? "1" : "0");
        telemetry.addData("Intake (Laser 2)", () -> laser2.getState() ? "1" : "0");
    }

    public void forward() {
        VEL = FWD;
    }

    public void reverse() {
        VEL = REV;
    }

    public void hold() {
        VEL = HOLD;
    }

    public void stop() {
        VEL = STOP;
    }

    public void artifactCount() {
        if (artifactDetected) {++artifactsInBot;}
        if (artifactsInBot >= 3) {robotIsFull = true;}
    }

    public void detectArtifact() {
        if (currentState1 && !previousState1 || currentState2 && !previousState2) {
            artifactDetected = true;
        } else artifactDetected = false;
    }

    public void reset() {
        artifactsInBot = 0;
    }

    private void configure(MotorEx motor) {
        motor.setInverted(true);
        motor.stopAndResetEncoder();
        motor.setZeroPowerBehavior(FLOAT);
        motor.setRunMode(VelocityControl);
    }
}
