package net.staretta.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.staretta.RawrBot;
import net.staretta.businesslogic.services.ServerService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public abstract class BaseListener extends ListenerAdapter<PircBotX>
{
	private ModuleInfo moduleInfo;
	
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
	public void onMessage(MessageEvent<PircBotX> event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		HashMap<String, List<String>> commandList = moduleInfo.getCommands();
		
		if (settingsService.hasChannelModule(event.getBot().getConfiguration().getServerHostname(), event.getChannel().getName(),
				moduleInfo.getName()))
		{
			for (Entry<String, List<String>> command : commandList.entrySet())
			{
				if (isCommand(event.getMessage(), command.getKey()) && isHelp(s))
				{
					if (command.getValue().size() > 0)
					{
						for (String message : command.getValue())
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
	
	public abstract void OnMessage(MessageEvent<PircBotX> event);
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		HashMap<String, List<String>> commandList = moduleInfo.getCommands();
		for (Entry<String, List<String>> command : commandList.entrySet())
		{
			if (isCommand(event.getMessage(), command.getKey()) && isHelp(s))
			{
				if (command.getValue().size() > 0)
				{
					for (String message : command.getValue())
					{
						event.getUser().send().message(message);
					}
					return;
				}
			}
		}
		OnPrivateMessage(event);
	}
	
	public abstract void OnPrivateMessage(PrivateMessageEvent<PircBotX> event);
	
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
			return true;
		return false;
	}
	
	public boolean isHelp(String message)
	{
		if (message.toLowerCase().endsWith("-h") || message.toLowerCase().endsWith("-help") || message.toLowerCase().endsWith("--help"))
			return true;
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
}