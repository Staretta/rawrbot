package net.a1337ism.modules;

import net.a1337ism.RawrBot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dictionary extends ListenerAdapter
{
	private static Logger	logger	= LoggerFactory.getLogger(RawrBot.class);

	Dictionary()
	{
	}

	public void onMessage(MessageEvent event)
	{

	}

	private String getDefinition()
	{
		return "";
	}
}
