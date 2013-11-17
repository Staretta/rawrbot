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

    public static String getHttpPage(String url) throws IOException {
        String page = "";
        InputStream is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        page = readAll(rd);
        is.close();
        return page;
    }

    /**
     * Converts URL Connection response to a JSON Object
     * 
     * @return JSONObject json
     */
    public static JSONArray readJsonFromURL(String url) throws IOException, JSONException {
        JSONArray array = new JSONArray(getHttpPage(url));
        // JSONObject json = array.getJSONObject(0);
        return array;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        JSONObject json = new JSONObject(getHttpPage(url));
        return json;
    }

}