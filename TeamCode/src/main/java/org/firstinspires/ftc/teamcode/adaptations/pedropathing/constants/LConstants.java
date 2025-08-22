package org.firstinspires.ftc.teamcode.adaptations.pedropathing.constants;

import com.pedropathing.localization.Encoder;
import com.pedropathing.localization.constants.ThreeWheelConstants;

public class LConstants {
    static {
//        PinpointConstants.forwardY = (2832.8068 * 1.25984 * Math.PI) / 2000; //Using ticks * wheel diameter * π / ticks per revolution
//        PinpointConstants.strafeX = (1324.8535 * 1.25984 * Math.PI) / 2000;
//        PinpointConstants.distanceUnit = DistanceUnit.INCH;
//        PinpointConstants.hardwareMapName = "pinpoint";
//        PinpointConstants.useYawScalar = false;
//        PinpointConstants.yawScalar = 1.0;
//        PinpointConstants.useCustomEncoderResolution = false;
//        PinpointConstants.encoderResolution = GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;
//        PinpointConstants.customEncoderResolution = 13.26291192;
//        PinpointConstants.forwardEncoderDirection = GoBildaPinpointDriver.EncoderDirection.REVERSE;
//        PinpointConstants.strafeEncoderDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
        ThreeWheelConstants.forwardTicksToInches = .002;
        ThreeWheelConstants.strafeTicksToInches = -.002;
        ThreeWheelConstants.turnTicksToInches = -.002;
        ThreeWheelConstants.leftY = (-2809.5702 * 1.25984 * Math.PI) / 2000;
        ThreeWheelConstants.rightY = (2832.8068 * 1.25984 * Math.PI) / 2000;
        ThreeWheelConstants.strafeX = (1324.8535 * 1.25984 * Math.PI) / 2000;
        ThreeWheelConstants.leftEncoder_HardwareMapName = "driveBackLeft";
        ThreeWheelConstants.rightEncoder_HardwareMapName = "driveBackRight";
        ThreeWheelConstants.strafeEncoder_HardwareMapName = "driveFrontRight";
        ThreeWheelConstants.leftEncoderDirection = Encoder.FORWARD;
        ThreeWheelConstants.rightEncoderDirection = Encoder.FORWARD;
        ThreeWheelConstants.strafeEncoderDirection = Encoder.FORWARD;
    }
}




