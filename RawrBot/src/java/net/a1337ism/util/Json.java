package net.a1337ism.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Json {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Converts URL Connection response to a JSON Object
     * 
     * @return JSONObject json
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        // Grabs API from url, and puts it into InputStream
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            // Makes a string and stores it in jsonText
            String jsonText = readAll(rd);
            if (jsonText.startsWith("[")) {
                JSONArray array = new JSONArray(jsonText);
                JSONObject json = array.getJSONObject(0);
                return json;
            } else {
                // Makes a jsonObject out of the string
                JSONObject json = new JSONObject(jsonText);
                return json;
            }
        } finally {
            is.close();
        }
    }
}