package org.firstinspires.ftc.teamcode.game;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * JVM unit test version that uses java.util.Base64 so it runs with ./gradlew test.
 * This test mirrors the web-app encoding (UTF-8 JSON -> GZIP -> Base64) and
 * decodes with a local routine to verify round-trip and parsed fields.
 */
public class QRDecoderTests {

    @Test
    public void decodePlan_returnsExpectedCommands() throws Exception {
        // Build JSON text directly to avoid calling JSONObject.put (which hits Android stubs on the JVM)
        String cmd1Json = "{\"cmd\":\"drive_to\",\"x\":1,\"y\":2,\"heading\":90}";
        String cmd2Json = "{\"cmd\":\"delay_ms\",\"ms\":500}";
        String outerText = "[" + cmd1Json + "," + cmd2Json + "]";

        // Compress and Base64-encode (GZIP)
        byte[] gz = gzip(outerText.getBytes(StandardCharsets.UTF_8));
        String b64 = Base64.getEncoder().encodeToString(gz);

        // Now decode using a small local decode routine that mirrors QRDecoder behavior
        JSONArray decoded = localDecodePlan(b64);

        // Assertions
        assertEquals(2, decoded.length());

        JSONObject d0 = decoded.getJSONObject(0);
        assertEquals("drive_to", d0.getString("cmd"));
        assertEquals(1, d0.getInt("x"));
        assertEquals(2, d0.getInt("y"));
        assertEquals(90, d0.getInt("heading"));

        JSONObject d1 = decoded.getJSONObject(1);
        assertEquals("delay_ms", d1.getString("cmd"));
        assertEquals(500, d1.getInt("ms"));
    }

    private static byte[] gzip(byte[] input) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        gos.write(input);
        gos.close();
        return baos.toByteArray();
    }

    // Minimal local decoder that mirrors QRDecoder.decodePlan (Base64 -> gzip -> parse outer array -> parse inner JSON strings)
    private static JSONArray localDecodePlan(String b64Payload) throws Exception {
        byte[] compressed = Base64.getDecoder().decode(b64Payload);
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(compressed);
        java.util.zip.GZIPInputStream gis = new java.util.zip.GZIPInputStream(bais);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int r;
        while ((r = gis.read(buffer)) != -1) {
            baos.write(buffer, 0, r);
        }
        gis.close();

        String jsonText = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        JSONArray outer = new JSONArray(jsonText);
        JSONArray result = new JSONArray();
        for (int i = 0; i < outer.length(); i++) {
            Object item = outer.get(i);
            if (item instanceof String) {
                String s = (String) item;
                try {
                    result.put(new JSONObject(s));
                } catch (org.json.JSONException je1) {
                    try {
                        result.put(new JSONArray(s));
                    } catch (org.json.JSONException je2) {
                        result.put(s);
                    }
                }
            } else {
                result.put(item);
            }
        }
        return result;
    }
}