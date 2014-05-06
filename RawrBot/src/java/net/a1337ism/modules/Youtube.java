package net.a1337ism.modules;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.a1337ism.RawrBot;
import net.a1337ism.util.Config;
import net.a1337ism.util.MiscUtil;
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

public class Youtube extends ListenerAdapter
{
	// TODO: Move clientID to properties file.
	// TODO: Add Duration, Uploader, and short description of youtube video.
	// Probably requires making a new function for building http requests, and passing the list
	// for snippets, and video details.
	private Config			cfg				= new Config("././config.properties");
	private static Logger	logger			= LoggerFactory.getLogger(RawrBot.class);
	private String			regex			= "(?:https?:\\/\\/)?(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
	private Pattern			pattern			= Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	private String			apiKey			= cfg.getProperty("youtube_api_key");
	private HttpTransport	httpTransport	= new NetHttpTransport();
	private JsonFactory		jsonFactory		= new JacksonFactory();
	private YouTube			youtube;

	class VideoDetails
	{
		String	videoID;
		String	title;
		String	duration;
		String	fDuration;
		String	quality;
		String	description;
		String	channelTitle;
		String	dimension;

		VideoDetails()
		{

		}
	}

	/**
	 * Checks if the message has a valid YouTube URL
	 */
	private boolean isYouTubeURL(String link)
	{
		Matcher matcher = pattern.matcher(link);
		if (matcher.find())
			return true;
		return false;
	}

	/**
	 * Gets the YouTube video ID from the url<br>
	 * <br>
	 * URL: http://www.youtube.com/watch?v=wwJDhg-BLHM<br>
	 * ID: wwJDhg-BLHM<br>
	 */
	private String getYouTubeVideoID(String link)
	{
		String video_id = null;
		Matcher matcher = pattern.matcher(link);
		if (matcher.find())
		{
			String groupIndex1 = matcher.group(1);
			if (groupIndex1 != null && groupIndex1.length() == 11)
				video_id = groupIndex1;
		}
		return video_id;
	}

	private List<Video> getYouTubeAPI(String ID)
	{
		// Need to build our http request for Youtube's API
		List<Video> list = null;
		youtube = new YouTube.Builder(httpTransport, jsonFactory, new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException
			{
			}
		}).setApplicationName("RawrBot").build();
		try
		{
			YouTube.Videos.List videos = null;
			// videos = youtube.videos().list("snippet,contentDetails,statistics,fileDetails");
			videos = youtube.videos().list("snippet,contentDetails");
			videos.setKey(apiKey).setId(ID);
			VideoListResponse response = videos.execute();
			list = response.getItems();
		}
		catch (IOException e)
		{
			logger.info("IOException in YouTube.getYouTubeAPI: " + e.toString());
		}
		return list;
	}

	/**
	 * Gets the YouTube Video info using Google's API and the ID
	 */
	private VideoDetails getYouTubeVideoInfo(String link)
	{
		// Parse the ID from the URL, and if it's not null, then get the title.
		String ID = getYouTubeVideoID(link);
		VideoDetails videoDetails = new VideoDetails();

		if (ID != null)
		{
			List<Video> list = getYouTubeAPI(ID);
			if (!list.isEmpty())
			{
				videoDetails.title = list.get(0).getSnippet().getTitle();
				videoDetails.duration = list.get(0).getContentDetails().getDuration();
				videoDetails.fDuration = MiscUtil.durationFormat(MiscUtil.parsePeriodTime(videoDetails.duration));
				videoDetails.quality = list.get(0).getContentDetails().getDefinition();
				videoDetails.dimension = list.get(0).getContentDetails().getDimension();
				videoDetails.videoID = ID;
				videoDetails.channelTitle = list.get(0).getSnippet().getChannelTitle();
			}
		}
		return videoDetails;
	}

	public void onMessage(MessageEvent event) throws Exception
	{
		// If message is a youtube url and we have a youtube api key in the properties file.
		if (!apiKey.isEmpty() && isYouTubeURL(event.getMessage()))
		{
			// Get the title of the video, and message the channel.
			VideoDetails videoDetails = getYouTubeVideoInfo(event.getMessage());
			String message = "YouTube: " + videoDetails.title + " | Uploaded by " + videoDetails.channelTitle + " "
					+ videoDetails.fDuration + "[" + videoDetails.quality.toUpperCase() + "]";
			if (videoDetails.title != null)
				ircUtil.sendMessage(event, message);
		}
	}
}