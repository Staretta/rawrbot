package net.a1337ism.modules;

import net.a1337ism.RawrBot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class About extends ListenerAdapter
{
	private static Logger	logger	= LoggerFactory.getLogger(RawrBot.class);

	public void onMessage(MessageEvent event) throws Exception
	{
		if (event.getMessage().trim().toLowerCase().startsWith("!about")
				&& !RateLimiter.isRateLimited(event.getUser().getNick()))
		{

		}
	}

	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{

	}
}