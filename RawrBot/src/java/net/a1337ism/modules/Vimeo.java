package net.a1337ism.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.a1337ism.RawrBot;
import net.a1337ism.util.Json;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.ircUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vimeo extends ListenerAdapter {
    private Logger  logger  = LoggerFactory.getLogger(RawrBot.class);
    private String  regex   = "(https?://)?(www.)?(player.)?vimeo.com/([a-z]*/)*([0-9]{6,11})[?]?.*";
    private Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    private boolean isVimeoURL(String message) {
        Matcher matcher = pattern.matcher(message);
        if (matcher.find())
            return true;
        return false;
    }

    private String getVimeoVideoID(String message) {
        String video_id = null;
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String groupIndex5 = matcher.group(5);
            if (groupIndex5 != null) {
                video_id = groupIndex5;
            }
        }
        return video_id;
    }

    private String getVimeoTitle(JSONObject json) throws JSONException {
        return json.getString("title");
    }

    private int getVimeoDuration(JSONObject json) throws JSONException {
        return Integer.parseInt(json.getString("duration"));
    }

    private JSONObject getVimeoAPI(String ID) throws JSONException, IOException {
        String jsonURL = "http://vimeo.com/api/v2/video/" + ID + ".json";
        JSONArray jsonArray = Json.readJsonFromURL(jsonURL);
        JSONObject json = jsonArray.getJSONObject(0);
        // Object[] array = { json.get("title"), json.get("duration") };
        return json;
    }

    private List<String> getVimeoInfo(String link) {
        // Parse the ID from the URL, and if it's not null, then get the video information.
        String ID = getVimeoVideoID(link);
        List<String> list = new ArrayList<String>();
        if (ID != null) {
            try {
                JSONObject json = getVimeoAPI(ID);
                list.add(getVimeoTitle(json));
                list.add(MiscUtil.durationFormat(getVimeoDuration(json)));
                return list;
            } catch (JSONException e) {
                logger.error("JSONException in Vimeo.getVimeoTitle: " + e.toString());
            } catch (IOException e) {
                logger.error("IOException in Vimeo.getVimeoTitle: " + e.toString());
            }
        }
        return list;
    }

    public void onMessage(MessageEvent event) throws Exception {
        // If regex matches link in message, then message channel
        if (isVimeoURL(event.getMessage())) {
            List<String> info = getVimeoInfo(event.getMessage());
            String message = "Vimeo: " + info.get(0) + " " + info.get(1);
            if (info != null)
                ircUtil.sendMessage(event, message);
        }
    }
}