package net.staretta.businesslogic;

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
	public void onMessage(MessageEvent event)
	{
		String s = event.getMessage().trim().toLowerCase();
		if (s.startsWith(moduleInfo.getHelpCommand()) && (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help")))
		{
			if (!moduleInfo.getHelpMessage().isEmpty())
				ircUtil.sendMessage(event, moduleInfo.getHelpMessage());
		}
		else
		{
			OnMessage(event);
		}
	}
	
	public abstract void OnMessage(MessageEvent event);
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		String s = event.getMessage().trim().toLowerCase();
		if (s.endsWith("-h") || s.endsWith("-help") || s.endsWith("--help"))
		{
			if (!moduleInfo.getHelpMessage().isEmpty())
				ircUtil.sendMessage(event, moduleInfo.getHelpMessage());
		}
		else
		{
			OnPrivateMessage(event);
		}
	}
	
	public abstract void OnPrivateMessage(PrivateMessageEvent event);
}