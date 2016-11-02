package net.staretta.businesslogic;

import java.util.ArrayList;

import net.staretta.RawrBot;
import net.staretta.businesslogic.services.ServerService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseListener extends ListenerAdapter
{
	private ModuleInfo moduleInfo;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public ServerService settingsService;
	
	public BaseListener()
	{
		moduleInfo = setModuleInfo();
		
		settingsService = RawrBot.getAppCtx().getBean(ServerService.class);
	}
	
	protected abstract ModuleInfo setModuleInfo();
	
	public ModuleInfo getModuleInfo()
	{
		return moduleInfo;
	}
	
	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		ArrayList<Command> commands = moduleInfo.getCommands();
		
		String hostname = event.getBot().getServerHostname();
		String channel = event.getChannel().getName();
		
		// TODO: MOVE THIS INTO A GLOBAL CONCURRENT HASHMAP, AND ADD A RELOAD COMMAND TO REFRESH THE MAP.
		if (commands != null && settingsService.hasChannelModule(hostname, channel, moduleInfo.getName()))
		{
			for (Command command : commands)
			{
				if (isCommand(event.getMessage(), command.getCommand()) && isHelp(s))
				{
					if (command.getCommandHelp().size() > 0)
					{
						for (String message : command.getCommandHelp())
						{
							event.getChannel().send().message(message);
						}
						return;
					}
				}
			}
			OnMessage(event);
		}
	}
	
	public abstract void OnMessage(MessageEvent event);
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		ArrayList<Command> commands = moduleInfo.getCommands();
		for (Command command : commands)
		{
			if (isCommand(event.getMessage(), command.getCommand()) && isHelp(s))
			{
				if (command.getCommandHelp().size() > 0)
				{
					for (String message : command.getCommandHelp())
					{
						event.getUser().send().message(message);
					}
					return;
				}
			}
		}
		OnPrivateMessage(event);
	}
	
	public abstract void OnPrivateMessage(PrivateMessageEvent event);
	
	public boolean isCommand(String message, String command)
	{
		// Doing it this way versus just using .startswith because we want to be sure the command is actually the correct command.
		// To stop people from doing !rawretta when it should only work for !rawr
		String[] params = message.trim().split("\\s");
		if (params.length >= 1)
		{
			if (params[0].toLowerCase().equals(command))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isOption(String message, int i, String... option)
	{
		String[] params = message.trim().split("\\s");
		if (params.length >= 2)
		{
			for (String s : option)
			{
				if (params[i].toLowerCase().equals(s))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isOption(String message, String... option)
	{
		if (isOption(message, 1, option))
		{
			return true;
		}
		return false;
	}
	
	public boolean isHelp(String message)
	{
		message = message.trim().toLowerCase();
		if (message.endsWith("-h") || message.endsWith("-help") || message.endsWith("--help"))
		{
			return true;
		}
		return false;
	}
	
	public String removeCommand(String command, String message)
	{
		return removeCommands(new String[] { command }, message);
	}
	
	public String removeCommands(String[] commands, String message)
	{
		for (String command : commands)
		{
			if (isCommand(command, message))
			{
				return message.replaceFirst("(?i)" + command, "");
			}
		}
		return message;
	}
	
	public Logger getLogger()
	{
		return logger;
	}
}