package net.staretta.modules;

import java.lang.reflect.Field;
import java.util.List;

import net.staretta.RawrBot;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.services.SettingsService;
import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Help extends ListenerAdapter
{
	private static Logger	logger		= LoggerFactory.getLogger(Help.class);
	private SettingsService	settingsService;
	public static String	help		= "";
	public static String	helpCommand	= "!help !commands";

	public Help()
	{
		settingsService = RawrBot.applicationContext.getBean(SettingsService.class);
	}

	@Override
	public void onMessage(MessageEvent event)
	{
		if (ircUtil.isCommand(event, "!commands") || ircUtil.isCommand(event, "!help"))
		{
			if (RateLimiter.isRateLimited(event.getUser().getNick()))
				return;

			for (String line : helpCommand(event.getBot().getConfiguration().getServerHostname()))
				ircUtil.sendMessage(event, line);
		}
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		if (ircUtil.isCommand(event, "!commands") || ircUtil.isCommand(event, "!help"))
		{
			for (String line : helpCommand(event.getBot().getConfiguration().getServerHostname()))
				ircUtil.sendMessage(event, line);
		}
	}

	private String[] helpCommand(String server)
	{
		String commands = "";
		List<String> modules = settingsService.getServerSettings(server).getModules();
		for (String mod : modules)
		{
			Field field;
			String value;
			try
			{
				Class clazz = Class.forName("net.staretta.modules." + mod);
				field = clazz.getField("helpCommand");
				value = (String) field.get(clazz);
				if (!value.isEmpty())
					commands += value.toString() + " ";
			}
			catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException
					| IllegalAccessException e)
			{
				logger.error("Exception in Help.helpCommand: ", e);
			}
		}
		String[] commandHelp = { "Commands: " + commands, "For command specific help, type \"-help\" after a command." };
		return commandHelp;
	}
}