package net.staretta.modules;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.util.Json;
import net.staretta.businesslogic.util.ircUtil;

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
		moduleInfo.setHelpMessage("!quote : Says a random quote from the iheartquotes.com Database.");
		moduleInfo.setHelpCommand("!quote");
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		if (ircUtil.isCommand(event, "!quote") && !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			Object[] quote = null;
			quote = getQuote();
			
			if (quote != null)
				ircUtil.sendMessage(event, quote[0].toString());
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (ircUtil.isCommand(event, "!quote"))
		{
			Object[] quote = null;
			quote = getQuote();
			
			if (quote != null)
				ircUtil.sendMessage(event, quote[0].toString());
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