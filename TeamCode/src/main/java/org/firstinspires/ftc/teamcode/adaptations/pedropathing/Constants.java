package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.localization.Encoder;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Constants {

    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(13.063)
            .forwardZeroPowerAcceleration(-33.0731)
            .lateralZeroPowerAcceleration(-88.5791)
            .useSecondaryTranslationalPIDF(false)
            .useSecondaryHeadingPIDF(false)
            .useSecondaryDrivePIDF(false)
            .centripetalScaling(0.0005)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.15, 0, 0, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(3, 0, 0, 0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.015, 0, 0, 0.6, 0));

    public static MecanumConstants driveConstants = new MecanumConstants()
            .leftFrontMotorName("driveFrontLeft")
            .leftRearMotorName("driveBackLeft")
            .rightFrontMotorName("driveFrontRight")
            .rightRearMotorName("driveBackRight")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(65.8072)
            .yVelocity(49.9586);

    public static ThreeWheelConstants localizerConstants = new ThreeWheelConstants()
            .forwardTicksToInches(.002)
            .strafeTicksToInches(-.002)
            .turnTicksToInches(-.002)
            .leftPodY((-2809.5702 * 1.25984 * Math.PI) / 2000)
            .rightPodY((2832.8068 * 1.25984 * Math.PI) / 2000)
            .strafePodX((1324.8535 * 1.25984 * Math.PI) / 2000)
            .leftEncoder_HardwareMapName("driveBackLeft")
            .rightEncoder_HardwareMapName("driveBackRight")
            .strafeEncoder_HardwareMapName("driveFrontRight")
            .leftEncoderDirection(Encoder.FORWARD)
            .rightEncoderDirection(Encoder.FORWARD)
            .strafeEncoderDirection(Encoder.FORWARD);

    public static PathConstraints pathConstraints = new PathConstraints(
            0.995,
            500,
            1,
            1
    );

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .threeWheelLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}
