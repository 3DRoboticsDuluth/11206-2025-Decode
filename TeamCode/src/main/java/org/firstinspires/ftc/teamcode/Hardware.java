package org.firstinspires.ftc.teamcode;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection.UP;
import static com.seattlesolvers.solverslib.hardware.motors.Motor.GoBILDA.RPM_1150;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import org.openftc.easyopencv.OpenCvCamera;

import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.MagneticFlux;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class Hardware {


    public HardwareMap hardwareMap;

    public VoltageSensor batteryVoltageSensor;

    public IMU imu;
    public YawPitchRollAngles imuAngles;
    public AngularVelocity imuVelocities;

    public MotorGroup drive;
    public MotorEx driveFrontLeft;
    public MotorEx driveFrontRight;
    public MotorEx driveBackLeft;
    public MotorEx driveBackRight;

    public Servo deflector;
    public Servo sort;
    public Servo vision;
    public Servo pivot;
    public MotorEx intake;
    public CRServo conveyorLeft;
    public CRServo conveyorRight;
//    public Servo conveyorFront;
//    public Servo conveyorMiddle;
//    public Servo conveyorBack;

    public MotorEx depositLeft;
    public MotorEx depositRight;

    public Limelight3A limelight;

    public MagneticFlux magneticLimitSwitch;
    
    public RevBlinkinLedDriver lightsLeft;
    public RevBlinkinLedDriver lightsRight;
    public Servo lightsIndicator;

    public Hardware(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();
//
//        drive = new MotorGroup(
//            driveFrontLeft = new MotorEx(hardwareMap, "frontLeft", RPM_1150),
//            driveFrontRight = new MotorEx(hardwareMap, "fFrontRight", RPM_1150),
//            driveBackLeft = new MotorEx(hardwareMap, "backLeft", RPM_1150),
//            driveBackRight = new MotorEx(hardwareMap, "backRight", RPM_1150)
//        );
//
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(
            new IMU.Parameters(
                new RevHubOrientationOnRobot(RIGHT, UP)
            )
        );

//        deflector = hardwareMap.get(Servo.class, "deflector");
//
//        sort = hardwareMap.get(Servo.class, "sort");
//        vision = hardwareMap.get(Servo.class,"vision");
//        pivot = hardwareMap.get(Servo.class, "pivot");
//
//        intake = hardwareMap.get(MotorEx.class, "intake");
//
//        conveyorLeft = hardwareMap.get(CRServo.class, "conveyorLeft");
//        conveyorRight = hardwareMap.get(CRServo.class, "conveyorRight");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
//
//        lightsLeft = hardwareMap.get(RevBlinkinLedDriver.class, "lightsLeft");
//        lightsRight = hardwareMap.get(RevBlinkinLedDriver.class, "lightsRight");
//        lightsIndicator = hardwareMap.get(Servo.class, "lightsIndicator");

//        magneticLimitSwitch = hardwareMap.get(MagneticFlux.class, "magneticLimitSwitch");

        updateImuAnglesAndVelocities();
    }

    public void updateImuAnglesAndVelocities() {
//        try {
//            imuAngles = imu.getRobotYawPitchRollAngles();
//            imuVelocities = imu.getRobotAngularVelocity(RADIANS);
//        } catch (Exception e) {
//            Log.e("Hardware", "Error attempting to get IMU angles and velocities.", e);
//        }
    }
}
