package net.staretta.modules;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.util.Json;
import net.staretta.businesslogic.util.MiscUtil;
import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class Vimeo extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(Vimeo.class);
	private String regex = "(https?://)?(www.)?(player.)?vimeo.com/([a-z]*/)*([0-9]{6,11})[?]?.*";
	private Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Vimeo");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.2");
		return moduleInfo;
	}
	
	class VideoDetails
	{
		int videoID;
		String title;
		int duration;
		String fDuration;
		int plays;
		int comments;
		int likes;
		int width;
		int height;
		String username;
		String uploadDate;
		String description;
		
		VideoDetails()
		{
		}
	}
	
	private boolean isVimeoURL(String message)
	{
		Matcher matcher = pattern.matcher(message);
		if (matcher.find())
			return true;
		return false;
	}
	
	private String getVimeoVideoID(String message)
	{
		String video_id = null;
		Matcher matcher = pattern.matcher(message);
		if (matcher.find())
		{
			String groupIndex5 = matcher.group(5);
			if (groupIndex5 != null)
			{
				video_id = groupIndex5;
			}
		}
		return video_id;
	}
	
	private JsonNode getVimeoAPI(String ID) throws IOException
	{
		String jsonURL = "http://vimeo.com/api/v2/video/" + ID + ".json";
		JsonNode jsonArray = Json.readJsonFromUrl(jsonURL);
		JsonNode json = jsonArray.get(0);
		return json;
	}
	
	private VideoDetails getVimeoVideoDetails(String link)
	{
		// Parse the ID from the URL, and if it's not null, then get the video information.
		String ID = getVimeoVideoID(link);
		VideoDetails videoDetails = new VideoDetails();
		if (ID != null)
		{
			try
			{
				JsonNode json = getVimeoAPI(ID);
				if (json == null)
					return videoDetails;
				
				if (json.has("id"))
					videoDetails.videoID = json.get("id").asInt();
				if (json.has("title"))
					videoDetails.title = json.get("title").asText();
				if (json.has("duration"))
				{
					videoDetails.duration = Integer.parseInt(json.get("duration").asText());
					videoDetails.fDuration = MiscUtil.durationFormat(videoDetails.duration);
				}
				if (json.has("width"))
					videoDetails.width = json.get("width").asInt();
				if (json.has("height"))
					videoDetails.height = json.get("height").asInt();
				if (json.has("stats_number_of_plays"))
					videoDetails.plays = json.get("stats_number_of_plays").asInt();
				if (json.has("stats_number_of_likes"))
					videoDetails.likes = json.get("stats_number_of_likes").asInt();
				if (json.has("stats_number_of_comments"))
					videoDetails.comments = json.get("stats_number_of_comments").asInt();
				if (json.has("description"))
					videoDetails.description = json.get("description").asText();
				if (json.has("user_name"))
					videoDetails.username = json.get("user_name").asText();
				if (json.has("upload_date"))
					videoDetails.uploadDate = json.get("upload_date").asText();
				
				return videoDetails;
			}
			catch (Exception e)
			{
				logger.error("Exception in Vimeo.getVimeoVideoDetails: " + e);
			}
		}
		return videoDetails;
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		// If regex matches link in message, then message channel
		if (isVimeoURL(event.getMessage()))
		{
			VideoDetails videoDetails = getVimeoVideoDetails(event.getMessage());
			String videoSize = "[" + videoDetails.width + "x" + videoDetails.height + "]";
			String message = "Vimeo: " + videoDetails.title + " " + videoDetails.fDuration + videoSize;
			if (videoDetails.title != null)
				ircUtil.sendMessage(event, message);
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
	}
}