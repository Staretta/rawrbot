package net.a1337ism.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import net.a1337ism.RawrBot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Json {
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

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
     * Converts URL Connection response to a JSON Array
     * 
     * @return JSONArray json
     * @throws IOException
     */
    public static JSONArray readJsonFromURL(String url) throws JSONException, IOException {
        JSONArray array = new JSONArray(getHttpPage(url));
        return array;
    }

    /**
     * Converts URL Connection response to a JSON Object
     * 
     * @return JSONObject json
     * @throws IOException
     */
    public static JSONObject readJsonFromUrl(String url) throws JSONException, IOException {
        JSONObject json = new JSONObject(getHttpPage(url));
        return json;
    }

}