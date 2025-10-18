package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import java.util.List;

@TeleOp(name = "Pre-Match QR Scanner", group = "Setup")
public class PreMatchQRScannerOpMode extends LinearOpMode {

    private Hardware hardware;
    private String lastQr = null;
    private boolean aPressed = false;
    private boolean bPressed = false;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize hardware
        hardware = new Hardware(hardwareMap);

        // Start the Limelight
        hardware.limelight.start();

        // Force Limelight to use pipeline 0 (your QR code pipeline)
        hardware.limelight.pipelineSwitch(0);

        // Set a reasonable poll rate
        hardware.limelight.setPollRateHz(100);

        sleep(500); // allow pipeline to fully activate

        telemetry.addLine("=== Pre-Match QR Scanner ===");
        telemetry.addLine("Scanning continuously...");
        telemetry.addLine("Press A to lock current QR code");
        telemetry.addLine("Press B to clear locked QR");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Get latest result from Limelight
            LLResult result = hardware.limelight.getLatestResult();

            // Clear telemetry for fresh display
            telemetry.clear();
            telemetry.addLine("=== QR Scanner Active ===");
            telemetry.addLine();

            // Display current detection status
            if (result != null && result.isValid()) {
                telemetry.addData("Status", "✓ Limelight Connected");

                List<LLResultTypes.BarcodeResult> barcodes = result.getBarcodeResults();

                if (!barcodes.isEmpty()) {
                    telemetry.addLine();
                    telemetry.addLine("--- LIVE QR DETECTION ---");

                    for (int i = 0; i < barcodes.size(); i++) {
                        LLResultTypes.BarcodeResult barcode = barcodes.get(i);
                        telemetry.addData("QR " + (i+1) + " Data", barcode.getData());
                        telemetry.addData("QR " + (i+1) + " Type", barcode.getFamily());
                    }

                    // Press A to lock the first QR code
                    if (gamepad1.a && !aPressed) {
                        lastQr = barcodes.get(0).getData();
                        telemetry.addLine();
                        telemetry.addLine("✓ QR CODE LOCKED!");
                    }
                } else {
                    telemetry.addLine();
                    telemetry.addData("Live Detection", "No QR codes visible");
                }
            } else {
                telemetry.addData("Status", "✗ No Limelight data");
                telemetry.addLine("Check connection and pipeline");
            }

            // Button state tracking for debouncing
            aPressed = gamepad1.a;

            // Press B to clear locked QR
            if (gamepad1.b && !bPressed) {
                lastQr = null;
                telemetry.addLine();
                telemetry.addLine("✓ Locked QR cleared");
            }
            bPressed = gamepad1.b;

            // Always display locked QR at nthe bottom
            telemetry.addLine();
            telemetry.addLine("======================");
            telemetry.addData("LOCKED QR", lastQr != null ? lastQr : "None");
            telemetry.addLine("======================");

            telemetry.update();

            sleep(50);
        }
    }
}