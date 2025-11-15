package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.bylazar.configurables.annotations.Configurable;
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
        .mass(11.884)
        .forwardZeroPowerAcceleration(-39.6768)
        .lateralZeroPowerAcceleration(-77.5455);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 0.33, 1);

    public static MecanumConstants driveConstants = new MecanumConstants()
        .maxPower(1)
        .xVelocity(57.1558)
        .yVelocity(59.1667)
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
