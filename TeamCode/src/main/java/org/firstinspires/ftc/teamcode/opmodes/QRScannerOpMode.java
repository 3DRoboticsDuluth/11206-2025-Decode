package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.game.QRCodePipeline;
import org.openftc.easyopencv.*;

@TeleOp(name="QR Scanner", group="Test")
public class QRScannerOpMode extends LinearOpMode {

    private OpenCvWebcam webcam;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"),
                cameraMonitorViewId
        );

        QRCodePipeline pipeline = new QRCodePipeline();
        webcam.setPipeline(pipeline);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
                telemetry.addLine("Webcam streaming started");
                telemetry.update();
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Error", errorCode);
                telemetry.update();
            }
        });

        telemetry.addLine("Camera streaming...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            String qr = pipeline.getLastQr();
            if (qr != null) {
                telemetry.addData("QR Detected", qr);
            } else {
                telemetry.addLine("No QR yet");
            }
            telemetry.update();
            sleep(50);
        }
    }
}
