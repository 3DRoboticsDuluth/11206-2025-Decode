package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.game.QRDecoder;
import org.json.JSONArray;
import org.json.JSONException;

@TeleOp(name = "QRDecoder Test", group = "Test")
public class QRScannerTestOpMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("QRDecoder Test OpMode Initialized");
        telemetry.update();

        // Example: replace this with the actual QR string from your generator
        String qrBase64String = "H4sIAAAAAAAAA62PzQ4CIQyE36XnPaDe9lXEkEa6K5EFU/BnY3x3Ab1IOGzMniad9JtO9/CUcJy0hF6CZnMjFb2ETsIjWSLp/NUToTZuzNMLuh+MLj6YWKjJayqmI+TiBM+R8uKANlCFGhfxTIr9vewW7Tfr5TNZwkBqxJiwJcf/fC7ytb6tyeKspvBBQ35MiDq+2WG7Wodm/G5J/NBOP7wBOjJmyDECAAA";

        waitForStart();

        while (opModeIsActive()) {
            try {
                // Decode QR payload
                JSONArray decoded = QRDecoder.decodePlan(qrBase64String);

                telemetry.addData("Decoded JSON Length", decoded.length());
                for (int i = 0; i < decoded.length(); i++) {
                    telemetry.addData("Item " + i, decoded.get(i).toString());
                }
            } catch (JSONException e) {
                telemetry.addData("Decode Error", e.getMessage());
            }

            telemetry.update();
            sleep(500); // update twice per second
        }
    }
}
