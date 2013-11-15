package net.a1337ism.modules;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.a1337ism.RawrBot;
import net.a1337ism.util.Json;
import net.a1337ism.util.ircUtil;

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

    private String getVimeoTitle(String message) {
        String ID = getVimeoVideoID(message);
        if (ID != null) {
            String jsonURL = "http://vimeo.com/api/v2/video/" + ID + ".json";
            try {
                JSONObject json = Json.readJsonFromUrl(jsonURL);
                Object[] array = { json.get("title") };
                return array[0].toString();
            } catch (JSONException e) {
                logger.error("JSONException in Vimeo.getVimeoTitle: " + e.toString());
            } catch (IOException e) {
                logger.error("IOException in Vimeo.getVimeoTitle: " + e.toString());
            }
        }
        return null;
    }

    public void onMessage(MessageEvent event) throws Exception {
        // If regex matches link in message, then message channel
        if (isVimeoURL(event.getMessage())) {
            String title = getVimeoTitle(event.getMessage());
            String message = "Vimeo: " + title;
            if (title != null)
                ircUtil.sendMessage(event, message);
        }
    }
}