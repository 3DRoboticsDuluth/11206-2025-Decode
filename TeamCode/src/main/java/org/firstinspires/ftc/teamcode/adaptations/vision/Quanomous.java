package org.firstinspires.ftc.teamcode.adaptations.vision;

import static org.firstinspires.ftc.teamcode.game.Config.config;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.qualcomm.robotcore.util.ReadWriteFile;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

@SuppressLint("SdCardPath")
public class Quanomous {
    // H4sIAAAAAAAAA32PwQoCMQxE/yXnHrrX/opICXZci91W2qAu4r9vdkUoi3gZJpk3gRxedJoCOQo13uGlkKEnOWto3vQCDjGP6t/mi8YsfIWv5aHwqm74F3bNgMSzn5pGKm6wtgsrErjBjyygvnMrLcpaKQG6yOCqUytVoMCZU8MvXGKC//yy2f1D+xPHBcupcmIMAQAA

    //[
    //    {
    //        "cmd": "drive_to",
    //        "x": 0,
    //        "y": 0,
    //        "heading": 0
    //    },
    //    {
    //        "cmd": "intake_row",
    //        "row": 1
    //    },
    //    {
    //        "cmd": "intake_row",
    //        "row": 0
    //    },
    //    {
    //        "cmd": "delay_ms",
    //        "ms": 1000
    //    },
    //    {
    //        "cmd": "release_gate"
    //    },
    //    {
    //        "cmd": "deposit",
    //        "mode": "near",
    //        "sorted": false
    //    },
    //    {
    //        "cmd": "deposit",
    //        "tile_x": 0,
    //        "tile_y": 0,
    //        "heading": 0,
    //        "sorted": false
    //    }
    //]

    private static final String BLOCKLY_DIR = "/sdcard/FIRST/blockly/";

    public static void process(String data) {
        String jsonText = decode(data);
        JSONArray jsonArray = parse(jsonText);
        String name = getMatchNumberFilename();
        save(name, jsonArray);
        config.quanomous = name;
    }

    public static String decode(String data) {
        try {
            byte[] bytes = Base64.decode(data, Base64.NO_WRAP);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try (GZIPInputStream gis = new GZIPInputStream(bais);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                for (int r; (r = gis.read(buf)) != -1;) baos.write(buf, 0, r);
                return baos.toString(StandardCharsets.UTF_8.name());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray parse(String jsonText) {
        try {
            return new JSONArray(jsonText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(String name, JSONArray jsonArray) {
        try {
            String json = jsonArray.toString(2);
            File file = new File(BLOCKLY_DIR + name);
            ReadWriteFile.writeFile(file, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray load(String name) {
        try {
            File file = new File(BLOCKLY_DIR + name);
            String jsonText = ReadWriteFile.readFile(file);
            return new JSONArray(jsonText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String prev() {
        // TODO: Get prev filename.
        return null;
    }

    public static String next() {
        // TODO: Get next filename.
        return null;
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    public static String getMatchNumberFilename() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return String.format("%s-match-%d.json", timestamp, 0 /*config.matchNumber*/);
    }
}
