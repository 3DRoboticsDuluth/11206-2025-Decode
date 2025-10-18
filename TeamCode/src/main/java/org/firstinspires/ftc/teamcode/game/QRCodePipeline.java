package org.firstinspires.ftc.teamcode.game;

import android.graphics.Bitmap;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.RGBLuminanceSource;

import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.Mat;
import org.opencv.android.Utils;

public class QRCodePipeline extends OpenCvPipeline {
    private volatile String lastQr = null;

    @Override
    public Mat processFrame(Mat input) {
        try {
            // Convert Mat to Bitmap
            Bitmap bmp = Bitmap.createBitmap(input.cols(), input.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(input, bmp);

            // ZXing decoding
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = new int[width * height];
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);

            LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decodeWithState(binaryBitmap);
            if (result != null) {
                lastQr = result.getText();
            }
        } catch (Exception ignored) {}
        return input; // optionally you can draw on the frame
    }

    public String getLastQr() {
        return lastQr;
    }
}
