package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.FilteredPIDFCoefficients;
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
    private static double tX = 1;
    private static double hX = .5;
    private static double dX = 1;

    //private static double tX = 2.0;
    //private static double hX = 1.5;
    //private static double dX = 8.0;

    //private static double tX = 2.0;
    //private static double hX = 2.0;
    //private static double dX = 2.0;

    //private static double tX = 2.0;
    //private static double hX = 1.5;
    //private static double dX = 2.0;

    public static FollowerConstants followerConstants = new FollowerConstants()
        .mass(12.5628)
        .forwardZeroPowerAcceleration(-24.591773413810188)
        .lateralZeroPowerAcceleration(-76.0984478775747)
        .translationalPIDFCoefficients(new PIDFCoefficients(0.1 * tX, 0 * tX, 0 * tX, 0.015 * tX))
        .headingPIDFCoefficients(new PIDFCoefficients(1 * hX, 0 * hX, 0 * hX, 0.01 * hX))
        .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.025 * dX, 0 * dX, 0.00001 * dX, 0.6 * dX, 0.01 * dX));

    // TODO: Retry breaking strength (0.80, 0.90, 0.95, 0.99)
    public static PathConstraints pathConstraints = new PathConstraints(0.995, 100, .7, 1);

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
