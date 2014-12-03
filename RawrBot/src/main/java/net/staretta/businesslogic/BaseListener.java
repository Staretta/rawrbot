package net.staretta.businesslogic;

import java.util.HashMap;
import java.util.Map.Entry;

import net.staretta.businesslogic.util.ircUtil;

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
	public void onMessage(MessageEvent event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		HashMap<String, String> commandList = moduleInfo.getCommands();
		for (Entry<String, String> command : commandList.entrySet())
		{
			if (ircUtil.isCommand(event, command.getKey()) && (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help")))
			{
				if (!command.getValue().isEmpty())
				{
					ircUtil.sendMessage(event, command.getValue());
					return;
				}
			}
		}
		OnMessage(event);
	}
	
	public abstract void OnMessage(MessageEvent event);
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{
		String s = event.getMessage().trim().toLowerCase();
		HashMap<String, String> commandList = moduleInfo.getCommands();
		for (Entry<String, String> command : commandList.entrySet())
		{
			if (ircUtil.isCommand(event, command.getKey()) && (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help")))
			{
				if (!command.getValue().isEmpty())
				{
					ircUtil.sendPrivateMessage(event, command.getValue());
					return;
				}
			}
		}
		OnPrivateMessage(event);
	}
	
	public abstract void OnPrivateMessage(PrivateMessageEvent event);
}