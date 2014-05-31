package net.staretta.modules;

import java.io.IOException;

import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.util.Json;
import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class Quote extends ListenerAdapter
{
	public static String	help		= "!quote : Says a random quote from the iheartquotes.com Database.";
	public static String	helpCommand	= "!quote";

	public void onMessage(MessageEvent event) throws Exception
	{
		if (ircUtil.isCommand(event, "!quote"))
		{
			// If they are rate limited, then return.
			if (RateLimiter.isRateLimited(event.getUser().getNick()))
				return;

			if (ircUtil.isCommand(event, "-help") || ircUtil.isCommand(event, "-h"))
			{
				// If message ends with -help or -h, then send them help information
				ircUtil.sendMessage(event, help);
			}
			else
			{
				// Throw it into a variable
				Object[] quote = null;
				quote = getQuote();

				// If it's not null, send them a quote
				if (quote != null)
					ircUtil.sendMessage(event, quote[0].toString());
			}
		}
	}

	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{
		if (ircUtil.isCommand(event, "!quote"))
		{
			if (ircUtil.isCommand(event, "-help") || ircUtil.isCommand(event, "-h"))
			{
				// If message ends with -help or -h, then send them help information
				ircUtil.sendMessage(event, help);
			}
			else
			{
				// Throw it into a variable
				Object[] quote = null;
				quote = getQuote();

				// If it's not null, send them a quote
				if (quote != null)
					ircUtil.sendMessage(event, quote[0].toString());
			}
		}
	}

	private Object[] getQuote() throws IOException
	{
		// grabs JSONobject and stores it into json for us to read from
		JsonNode json = Json.readJsonFromUrl("http://www.iheartquotes.com/api/v1/random?"
				+ "format=json&max_lines=1&max_characters=510&source=oneliners");
		// stores the specific values I want into an array to be used later.
		Object[] array = { json.get("quote").asText(), json.get("link").asText() };
		return array;
	}
}