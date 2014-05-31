package net.staretta.modules;

import java.lang.reflect.Field;
import java.util.List;

import net.staretta.RawrBot;
import net.staretta.businesslogic.ModuleInfo;
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
	private static Logger		logger	= LoggerFactory.getLogger(Help.class);
	private SettingsService		settingsService;
	public static ModuleInfo	moduleInfo;

	public Help()
	{
		settingsService = RawrBot.applicationContext.getBean(SettingsService.class);
		moduleInfo = new ModuleInfo();
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setHelpCommand("!help !commands");
		moduleInfo.setName("Help");
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
		List<String> modules = settingsService.getServerModules(server);
		for (String mod : modules)
		{
			Field field;
			ModuleInfo value;
			try
			{
				Class clazz = Class.forName("net.staretta.modules." + mod);
				field = clazz.getField("moduleInfo");
				value = (ModuleInfo) field.get(clazz);
				if (!value.getHelpCommand().isEmpty())
					commands += value.getHelpCommand() + " ";
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