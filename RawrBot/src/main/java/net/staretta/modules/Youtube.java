package net.staretta.modules;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.entity.GlobalConfigEntity;
import net.staretta.businesslogic.services.ServerService;
import net.staretta.businesslogic.util.Colors;
import net.staretta.businesslogic.util.MiscUtil;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class Youtube extends BaseListener
{
	// TODO: Add Duration, Uploader, and short description of youtube video.
	// Probably requires making a new function for building http requests, and passing the list
	// for snippets, and video details.
	private String regex = "(?:https?:\\/\\/)?(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
	private Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	private String apiKey = "";
	private HttpTransport httpTransport = new NetHttpTransport();
	private JsonFactory jsonFactory = new JacksonFactory();
	private YouTube youtube;
	private ServerService serverService;
	
	public Youtube()
	{
		serverService = RawrBot.getAppCtx().getBean(ServerService.class);
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Youtube");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.2");
		return moduleInfo;
	}
	
	class VideoDetails
	{
		String videoID;
		String title;
		String duration;
		String fDuration;
		String quality;
		String description;
		String channelTitle;
		String dimension;
		
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
		youtube = new YouTube.Builder(httpTransport, jsonFactory, new HttpRequestInitializer()
		{
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
			getLogger().info("IOException in YouTube.getYouTubeAPI: " + e);
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
	
	private void init(String server) throws NullPointerException
	{
		GlobalConfigEntity config = serverService.getGlobalConfig(server);
		if (config != null)
			apiKey = config.getYoutubeApiKey();
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		// If message is a youtube url and we have a youtube api key in the properties file.
		if (isYouTubeURL(event.getMessage()))
		{
			// if we don't have an API key set, then try and get it from the db.
			if (apiKey.isEmpty())
			{
				init(event.getBot().getServerHostname());
			}
			
			if (!apiKey.isEmpty())
			{
				// Get the title of the video, and message the channel.
				VideoDetails videoDetails = getYouTubeVideoInfo(event.getMessage());
				String message = Colors.add(Colors.BLACK, Colors.WHITE_BG, Colors.BOLD) + "You" + Colors.add(Colors.WHITE, Colors.RED_BG)
						+ "Tube" + Colors.NORMAL + ": " + videoDetails.title + " " + videoDetails.fDuration + "["
						+ videoDetails.quality.toUpperCase() + "]";
				if (videoDetails.title != null)
				{
					event.getChannel().send().message(message);
				}
			}
		}
	}
	
	@Override
	public void onAction(ActionEvent event) throws Exception
	{
		// If message is a youtube url.
		if (isYouTubeURL(event.getMessage()))
		{
			// if we don't have an API key set, then try and get it from the db.
			if (apiKey.isEmpty())
			{
				init(event.getBot().getServerHostname());
			}
			
			if (!apiKey.isEmpty())
			{
				// Get the title of the video, and message the channel.
				VideoDetails videoDetails = getYouTubeVideoInfo(event.getMessage());
				String message = Colors.add(Colors.BLACK, Colors.WHITE_BG, Colors.BOLD) + "You" + Colors.add(Colors.WHITE, Colors.RED_BG)
						+ "Tube" + Colors.NORMAL + ": " + videoDetails.title + " " + videoDetails.fDuration + "["
						+ videoDetails.quality.toUpperCase() + "]";
				if (videoDetails.title != null)
				{
					event.getChannel().send().message(message);
				}
			}
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
	}
	
	public String getApiKey()
	{
		return apiKey;
	}
	
	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}
}