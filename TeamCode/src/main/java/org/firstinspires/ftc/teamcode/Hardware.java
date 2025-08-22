package org.firstinspires.ftc.teamcode;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection.UP;
import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS;

import android.util.Log;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class Hardware {
    public HardwareMap hardwareMap;

    public VoltageSensor batteryVoltageSensor;

    public IMU imu;
    public YawPitchRollAngles imuAngles;
    public AngularVelocity imuVelocities;

    public Limelight3A limelight;

    public Hardware(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(
            new IMU.Parameters(
                new RevHubOrientationOnRobot(RIGHT, UP)
            )
        );

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        updateImuAnglesAndVelocities();
    }

    public void updateImuAnglesAndVelocities() {
        try {
            imuAngles = imu.getRobotYawPitchRollAngles();
            imuVelocities = imu.getRobotAngularVelocity(RADIANS);
        } catch (Exception e) {
            Log.e("Hardware", "Error attempting to get IMU angles and velocities.", e);
        }
    }
}
