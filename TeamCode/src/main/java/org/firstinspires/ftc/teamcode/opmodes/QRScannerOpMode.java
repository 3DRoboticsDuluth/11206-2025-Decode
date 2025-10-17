package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Bitmap;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.game.QRDecoder;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Result;

import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@TeleOp(name = "Limelight QR Scanner (Auto-Switch)", group = "Competition")
public class QRScannerOpMode extends LinearOpMode {

    private OpenCvWebcam webcam;
    private final AtomicReference<Bitmap> capturedBitmap = new AtomicReference<>(null);
    private volatile String lastQrString = null;
    private volatile JSONArray decodedPlan = null;
    private boolean qrDetected = false;
    private boolean autoSwitchEnabled = true;

    // Limelight configuration - adjust if needed
    private static final String LIMELIGHT_HOST = "limelight.local";
    private static final int LIMELIGHT_PORT = 5807;
    private static final String CAMERA_NAME = "limelight";

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize webcam
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        WebcamName camName = hardwareMap.get(WebcamName.class, CAMERA_NAME);
        webcam = OpenCvCameraFactory.getInstance().createWebcam(camName, cameraMonitorViewId);

        telemetry.addLine("Opening camera...");
        telemetry.update();

        webcam.openCameraDevice();
        webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);

        telemetry.addLine("=== Limelight QR Scanner ===");
        telemetry.addLine("A: Scan QR code");
        telemetry.addLine("B: Manual switch to smart mode");
        telemetry.addLine("X: Toggle auto-switch");
        telemetry.addData("Auto-switch", autoSwitchEnabled ? "ENABLED" : "DISABLED");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Toggle auto-switch on X
            if (gamepad1.x) {
                autoSwitchEnabled = !autoSwitchEnabled;
                telemetry.addData("Auto-switch", autoSwitchEnabled ? "ENABLED" : "DISABLED");
                telemetry.update();
                sleep(300); // debounce
            }

            // Capture and scan QR on button A
            if (gamepad1.a) {
                scanQRCode();
                sleep(600); // debounce
            }

            // Manual switch to smart mode on B
            if (gamepad1.b) {
                switchToSmartMode();
                sleep(300); // debounce
            }

            // Display status
            updateTelemetry();
            sleep(50);
        }

        // Cleanup
        cleanupCamera();
    }

    private void scanQRCode() {
        telemetry.addLine("Capturing frame...");
        telemetry.update();

        // Clear previous bitmap
        capturedBitmap.set(null);

        // Request frame via callback
        // Create a Consumer that will store the bitmap when it arrives
        Consumer<Bitmap> bitmapHandler = new Consumer<Bitmap>() {
            @Override
            public void accept(Bitmap bitmap) {
                capturedBitmap.set(bitmap);
            }
        };

        // Wrap it in a Continuation and pass to getFrameBitmap
        webcam.getFrameBitmap(Continuation.createTrivial(bitmapHandler));

        // Wait for bitmap to arrive
        Bitmap frame = waitForBitmap(1500);
        if (frame == null) {
            telemetry.addLine("No frame received (timeout)");
            telemetry.update();
            return;
        }

        // Process frame with ZXing
        try {
            int width = frame.getWidth();
            int height = frame.getHeight();
            int[] pixels = new int[width * height];
            frame.getPixels(pixels, 0, width, 0, 0, width, height);

            LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = null;
            try {
                result = new MultiFormatReader().decodeWithState(binaryBitmap);
            } catch (Exception e) {
                // No QR found
            }

            if (result != null) {
                lastQrString = result.getText();
                qrDetected = true;

                telemetry.addLine("QR Code Detected!");
                telemetry.addData("Raw Data Length", lastQrString.length());

                // Decode using QRDecoder
                try {
                    decodedPlan = QRDecoder.decodePlan(lastQrString);
                    telemetry.addLine("QR Successfully Decoded!");
                    telemetry.addData("Commands Found", decodedPlan.length());

                    // Display first few commands (limited for telemetry)
                    for (int i = 0; i < Math.min(3, decodedPlan.length()); i++) {
                        telemetry.addData("Cmd " + (i+1), decodedPlan.get(i).toString());
                    }
                    if (decodedPlan.length() > 3) {
                        telemetry.addLine("... (more commands)");
                    }

                    // Auto-switch if enabled
                    if (autoSwitchEnabled) {
                        telemetry.addLine("Auto-switching to smart mode...");
                        telemetry.update();
                        sleep(500);
                        boolean switched = switchToSmartMode();
                        if (switched) {
                            telemetry.addLine("Successfully switched! Ready for match.");
                            // Optionally stop OpMode here
                            // requestOpModeStop();
                        }
                    }

                } catch (JSONException e) {
                    telemetry.addLine("QR Decode Error!");
                    telemetry.addData("Error", e.getMessage());
                    Log.e("QRScanner", "QRDecoder error", e);
                }
            } else {
                telemetry.addLine("No QR code detected in frame");
                telemetry.addLine("Make sure QR is clearly visible");
            }
        } catch (Exception e) {
            telemetry.addLine("Frame Processing Error!");
            telemetry.addData("Error", e.getMessage());
            Log.e("QRScanner", "Frame processing exception", e);
        }

        telemetry.update();
    }

    private boolean switchToSmartMode() {
        telemetry.addLine("Switching Limelight to smart mode...");
        telemetry.update();

        // Try multiple possible endpoints
        String[] endpoints = {
                String.format("http://%s:%d/set?camMode=0", LIMELIGHT_HOST, LIMELIGHT_PORT),
                String.format("http://%s:%d/api/set?camMode=0", LIMELIGHT_HOST, LIMELIGHT_PORT)
        };

        for (String endpoint : endpoints) {
            if (tryHttpRequest(endpoint)) {
                telemetry.addLine("Limelight switched to smart mode!");
                telemetry.addData("Endpoint", endpoint);
                telemetry.update();
                return true;
            }
        }

        telemetry.addLine("Failed to switch Limelight mode");
        telemetry.addLine("Check network connection & Limelight IP");
        telemetry.update();
        return false;
    }

    private boolean tryHttpRequest(String urlString) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.connect();

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                is = conn.getInputStream();
                while (is.read() != -1) {} // consume response
                return true;
            } else {
                Log.w("QRScanner", "HTTP returned code " + code + " for " + urlString);
                return false;
            }
        } catch (Exception e) {
            Log.w("QRScanner", "Failed endpoint " + urlString + ": " + e.getMessage());
            return false;
        } finally {
            try { if (is != null) is.close(); } catch (Exception ignored) {}
            if (conn != null) conn.disconnect();
        }
    }

    private Bitmap waitForBitmap(long timeoutMs) {
        final long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline && opModeIsActive()) {
            Bitmap bmp = capturedBitmap.getAndSet(null);
            if (bmp != null) return bmp;
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException ignored) {}
        }
        return capturedBitmap.getAndSet(null);
    }

    private void updateTelemetry() {
        telemetry.addLine("=== Status ===");
        telemetry.addData("Auto-Switch", autoSwitchEnabled ? "ON" : "OFF");

        if (qrDetected && lastQrString != null) {
            telemetry.addLine("QR Loaded");
            if (decodedPlan != null) {
                telemetry.addData("Commands", decodedPlan.length());
            }
        } else {
            telemetry.addLine("No QR detected yet");
        }

        telemetry.addLine();
        telemetry.addLine("Controls:");
        telemetry.addLine("  A: Scan QR");
        telemetry.addLine("  B: Switch to smart mode");
        telemetry.addLine("  X: Toggle auto-switch");
        telemetry.update();
    }

    private void cleanupCamera() {
        try {
            webcam.stopStreaming();
        } catch (Exception e) {
            Log.w("QRScanner", "Error stopping stream", e);
        }
        try {
            webcam.closeCameraDevice();
        } catch (Exception e) {
            Log.w("QRScanner", "Error closing camera", e);
        }
    }
}