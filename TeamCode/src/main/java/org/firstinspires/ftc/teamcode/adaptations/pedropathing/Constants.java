package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Configurable
public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
        .mass(12.5628)
        .forwardZeroPowerAcceleration(-24.591773413810188)
        .lateralZeroPowerAcceleration(-76.0984478775747);
//        .translationalPIDFCoefficients(new PIDFCoefficients(0.05, 0, 0, 0.015));

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);
    public static MecanumConstants driveConstants = new MecanumConstants()
        .maxPower(1)
        .xVelocity(73.62513937161664)
        .yVelocity(56.98721866157111)
        .leftFrontMotorName("driveFrontLeft")
        .rightFrontMotorName("driveFrontRight")
        .leftRearMotorName("driveBackLeft")
        .rightRearMotorName("driveBackRight");

    public static PinpointConstants localizerConstants = new PinpointConstants()
        .forwardPodY(4.7244)
        .strafePodX(1.996)
        .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
        .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
            .pathConstraints(pathConstraints)
            .mecanumDrivetrain(driveConstants)
            .pinpointLocalizer(localizerConstants)
            .build();
    }
}
