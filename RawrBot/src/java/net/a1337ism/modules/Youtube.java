package net.a1337ism.modules;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.a1337ism.RawrBot;
import net.a1337ism.util.ircUtil;

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
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class Youtube extends ListenerAdapter {
    // TODO: Move clientID to properties file.
    // TODO: Add Duration, Uploader, and short description of youtube video.
    // Probably requires making a new function for building http requests, and passing the list
    // for snippets, and video details.
    private static Logger logger        = LoggerFactory.getLogger(RawrBot.class);
    private String        patternURL    = "^(?:https?:\\/\\/)?(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
    private Pattern       cPatternURL   = Pattern.compile(patternURL, Pattern.CASE_INSENSITIVE);
    private String        clientID      = "AIzaSyAnJew19gmHRP2KBBIuUyhFoCcwSQiPDs0";
    private HttpTransport httpTransport = new NetHttpTransport();
    private JsonFactory   jsonFactory   = new JacksonFactory();
    private YouTube       youtube;

    /**
     * Checks if the message has a URL
     */
    private boolean isURL(String link) {
        Matcher matcher = cPatternURL.matcher(link);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * Gets the youtube video ID from the url<br>
     * <br>
     * URL: http://www.youtube.com/watch?v=wwJDhg-BLHM<br>
     * ID: wwJDhg-BLHM<br>
     */
    private String getVideoID(String link) {
        String video_id = null;
        Matcher matcher = cPatternURL.matcher(link);
        if (matcher.find()) {
            String groupIndex1 = matcher.group(1);
            if (groupIndex1 != null && groupIndex1.length() == 11) {
                video_id = groupIndex1;
            }
        }
        return video_id;
    }

    /**
     * Gets the YouTube title using Google's API and the ID
     */
    private String getTitle(String link) {
        // Need to build our http request for Youtube's API
        youtube = new YouTube.Builder(httpTransport, jsonFactory, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("RawrBot").build();

        // Parse the ID from the URL, and if it's not null, then get the title.
        String ID = getVideoID(link);
        if (ID != null) {
            try {
                YouTube.Videos.List videos = null;
                videos = youtube.videos().list("snippet");
                videos.setKey(clientID).setId(ID);
                // videos.setId(ID);
                VideoListResponse response = videos.execute();
                List<Video> list = response.getItems();

                if (!list.isEmpty()) {
                    return list.get(0).getSnippet().getTitle();
                }
            } catch (IOException e) {
                logger.info("IOException in YouTube.GetTitle: " + e.toString());
            }
        }
        return null;
    }

    public void onMessage(MessageEvent event) throws Exception {
        // If message is a youtube url
        if (isURL(event.getMessage())) {
            // Get the title of the video, and message the channel.
            String title = getTitle(event.getMessage());
            String message = "YouTube: " + title;
            if (title != null)
                ircUtil.sendMessage(event, message);
        }
    }
}