package org.firstinspires.ftc.teamcode.game;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * QRDecoder
 *
 * Decodes a Base64 + GZIP compressed payload from the FTC QR Generator web app.
 * Returns a JSONArray where each element is typically a JSONObject representing a command.
 */
public class QRDecoder {

    /**
     * Decode a Base64 + GZIP string from the QR code.
     *
     * @param b64Payload The Base64-encoded, GZIP-compressed string from the web app QR code.
     * @return JSONArray of parsed command objects (JSONObjects/JSONArrays/Strings).
     * @throws JSONException If JSON parsing fails.
     */
    public static JSONArray decodePlan(String b64Payload) throws JSONException {
        try {
            // Decode Base64 to raw gzip bytes
            byte[] compressed = Base64.decode(b64Payload, Base64.DEFAULT);

            // Decompress GZIP
            ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int read;
            while ((read = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            gis.close();

            String jsonText = new String(baos.toByteArray(), StandardCharsets.UTF_8);

            // Outer structure is an array of strings (each string is a JSON string of a command)
            JSONArray outer = new JSONArray(jsonText);
            JSONArray result = new JSONArray();

            for (int i = 0; i < outer.length(); i++) {
                Object item = outer.get(i);

                if (item instanceof String) {
                    String s = (String) item;
                    // Try to parse the inner string as JSON object or array
                    try {
                        JSONObject obj = new JSONObject(s);
                        result.put(obj);
                    } catch (JSONException je1) {
                        try {
                            JSONArray arr = new JSONArray(s);
                            result.put(arr);
                        } catch (JSONException je2) {
                            // Not JSON — store the raw string
                            result.put(s);
                        }
                    }
                } else {
                    // If outer array contains objects/primitives directly, pass through
                    result.put(item);
                }
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new JSONException("Failed to decompress QR payload: " + e.getMessage());
        } catch (JSONException e) {
            // Re-throw JSON errors to caller
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JSONException("Failed to decode QR payload: " + e.getMessage());
        }
    }
}
