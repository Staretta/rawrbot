package net.staretta.businesslogic;

import java.util.HashMap;
import java.util.Map.Entry;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public abstract class BaseListener extends ListenerAdapter<PircBotX>
{
	public ModuleInfo moduleInfo;

	public BaseListener()
	{
		moduleInfo = setModuleInfo();
	}

	protected abstract ModuleInfo setModuleInfo();

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		HashMap<String, String[]> commandList = moduleInfo.getCommands();
		for (Entry<String, String[]> command : commandList.entrySet())
		{
			if (isCommand(event.getMessage(), command.getKey())
					&& (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help")))
			{
				if (command.getValue().length > 0)
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

	public abstract void OnMessage(MessageEvent<PircBotX> event);

	@Override
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		HashMap<String, String[]> commandList = moduleInfo.getCommands();
		for (Entry<String, String[]> command : commandList.entrySet())
		{
			if (isCommand(event.getMessage(), command.getKey())
					&& (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help")))
			{
				if (command.getValue().length > 0)
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
}