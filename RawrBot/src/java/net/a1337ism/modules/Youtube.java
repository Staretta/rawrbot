package net.a1337ism.modules;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.a1337ism.RawrBot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;

public class Youtube extends ListenerAdapter {
    private static Logger logger        = LoggerFactory.getLogger(RawrBot.class);
    private String        patternURL    = "^(?:https?:\\/\\/)?(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
    private Pattern       cPatternURL   = Pattern.compile(patternURL, Pattern.CASE_INSENSITIVE);
    private String        patternID     = "^.*((youtu.be" + "\\/)"
                                                + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
    private Pattern       cPatternID    = Pattern.compile(patternID, Pattern.CASE_INSENSITIVE);
    private String        clientID      = "758626281673-84evpp47vmg3o8b6d31hu61h33lbr7cl.apps.googleusercontent.com";
    private String        clientSecret  = "x3mqoqMsPIMepj4McY2vP4Uw";
    private HttpTransport httpTransport = new NetHttpTransport();
    private JsonFactory   jsonFactory   = new JacksonFactory();
    private YouTube       youtube;

    private boolean isURL(String link) {
        Matcher matcher = cPatternURL.matcher(link);
        if (matcher.find()) {
            System.out.println(matcher.group());
            return true;
        }
        return false;
    }

    private String getVideoID(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {
            CharSequence input = youtubeUrl;
            Matcher matcher = cPatternID.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
                else if (groupIndex1 != null && groupIndex1.length() == 10)
                    video_id = "v" + groupIndex1;
            }
        }
        return video_id;
    }

    private String getTitle(String link) {
        youtube = new YouTube.Builder(httpTransport, jsonFactory, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("RawrBot").build();
        String ID = getVideoID(link);

        YouTube.Videos.List video = null;
        try {
            video = youtube.videos().list("id,snippet");
            video.setKey(clientID);
            video.setId(ID);
            VideoListResponse videoResponse = video.execute();
        } catch (IOException e) {

        }

        return null;
    }

    public void onMessage(MessageEvent event) throws Exception {
        // If message is a youtube url
        if (isURL(event.getMessage())) {
            // Get the title of the video, and message the channel.
            getTitle(event.getMessage());
            // ircUtil.sendMessage(event, getTitle(event.getMessage()));
        }
    }
}