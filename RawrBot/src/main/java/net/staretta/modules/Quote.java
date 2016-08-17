package net.staretta.modules;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.Command;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.util.Json;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class Quote extends BaseListener
{
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Quote");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.2");
		moduleInfo.addCommand(new Command("!quote", "!quote : Says a random quote from the iheartquotes.com Database."));
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if (isCommand(event.getMessage(), "!quote") && !RateLimiter.isRateLimited(event.getUser()))
		{
			Object[] quote = null;
			quote = getQuote();
			
			if (quote != null)
				event.getChannel().send().message(quote[0].toString());
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		if (isCommand(event.getMessage(), "!quote"))
		{
			Object[] quote = null;
			quote = getQuote();
			
			if (quote != null)
				event.getUser().send().message(quote[0].toString());
		}
	}
	
	private Object[] getQuote()
	{
		// grabs JSONobject and stores it into json for us to read from
		JsonNode json = Json.readJsonFromUrl("http://www.iheartquotes.com/api/v1/random?"
				+ "format=json&max_lines=1&max_characters=510&source=oneliners");
		// stores the specific values I want into an array to be used later.
		Object[] array = { json.get("quote").asText(), json.get("link").asText() };
		return array;
	}
}