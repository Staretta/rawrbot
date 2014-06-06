package net.staretta.businesslogic;

import java.util.List;

import net.staretta.businesslogic.util.ircUtil;

import org.javatuples.Pair;
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
		List<Pair<String, String>> commandList = moduleInfo.getCommands();
		for (Pair<String, String> command : commandList)
		{
			if (s.startsWith(command.getValue0()) && (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help")))
			{
				if (!command.getValue1().isEmpty())
				{
					ircUtil.sendMessage(event, command.getValue1());
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
		List<Pair<String, String>> commandList = moduleInfo.getCommands();
		for (Pair<String, String> command : commandList)
		{
			if (s.startsWith(command.getValue0()) && (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help")))
			{
				if (!command.getValue1().isEmpty())
				{
					ircUtil.sendMessage(event, command.getValue1());
					return;
				}
				
			}
		}
		OnPrivateMessage(event);
	}
	
	public abstract void OnPrivateMessage(PrivateMessageEvent event);
}