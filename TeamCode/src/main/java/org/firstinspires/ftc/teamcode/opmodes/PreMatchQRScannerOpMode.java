package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Result;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "Pre-Match QR Scanner", group = "Setup")
public class PreMatchQRScannerOpMode extends LinearOpMode {

    private static final String LIMELIGHT_HOST = "limelight.local"; // Update if needed

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("=== Pre-Match QR Scanner ===");
        telemetry.addLine("Press A to capture and decode QR code");
        telemetry.addLine("Press B to clear last QR");
        telemetry.update();

        String lastQr = null;

        waitForStart();

        while (opModeIsActive()) {

            // Capture QR when A is pressed
            if (gamepad1.a) {
                telemetry.addLine("Capturing frame from Limelight...");
                telemetry.update();

                String qr = grabAndDecodeQR();

                if (qr != null) {
                    lastQr = qr;
                    telemetry.addLine("QR Detected!");
                    telemetry.addData("QR Data", lastQr);
                } else {
                    telemetry.addLine("No QR code detected");
                }

                telemetry.update();
                sleep(1000); // simple debounce
            }

            // Clear last QR
            if (gamepad1.b) {
                lastQr = null;
                telemetry.addLine("Cleared last QR");
                telemetry.update();
                sleep(500);
            }

            // Show status
            telemetry.addLine("");
            telemetry.addData("Last QR", lastQr != null ? lastQr : "None");
            telemetry.update();

            TimeUnit.MILLISECONDS.sleep(50);
        }
    }

    private String grabAndDecodeQR() {
        try {
            URL url = new URL("http://" + LIMELIGHT_HOST + ":5800/stream.mjpg");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.connect();

            InputStream is = conn.getInputStream();
            Bitmap frame = BitmapFactory.decodeStream(is);
            is.close();
            conn.disconnect();

            if (frame == null) {
                Log.e("QRScanner", "No frame grabbed");
                return null;
            }

            int width = frame.getWidth();
            int height = frame.getHeight();
            int[] pixels = new int[width * height];
            frame.getPixels(pixels, 0, width, 0, 0, width, height);

            LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = null;
            try {
                result = new MultiFormatReader().decode(binaryBitmap);
            } catch (Exception e) {
                // No QR found
            }

            return result != null ? result.getText() : null;

        } catch (Exception e) {
            Log.e("QRScanner", "Failed to grab or decode QR", e);
            return null;
        }
    }
}
