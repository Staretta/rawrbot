package net.staretta.modules;

import java.util.HashMap;
import java.util.Map.Entry;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class Help extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v2.1");
		moduleInfo.addCommand("!help");
		moduleInfo.addCommand("!commands");
		moduleInfo.setName("Help");
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		if ((isCommand(event.getMessage(), "!commands") || isCommand(event.getMessage(), "!help"))
				&& !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			ImmutableSet<Listener<PircBotX>> listeners = event.getBot().getConfiguration().getListenerManager().getListeners();
			for (String message : helpCommand(listeners))
				event.getChannel().send().message(message);
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (isCommand(event.getMessage(), "!commands") || isCommand(event.getMessage(), "!help"))
		{
			ImmutableSet<Listener<PircBotX>> listeners = event.getBot().getConfiguration().getListenerManager().getListeners();
			for (String message : helpCommand(listeners))
				event.getUser().send().message(message);
		}
	}
	
	private String[] helpCommand(ImmutableSet<Listener<PircBotX>> modules)
	{
		String commands = "";
		for (Listener<PircBotX> mod : modules)
		{
			if (BaseListener.class.isAssignableFrom(mod.getClass()))
			{
				BaseListener listener = (BaseListener) mod;
				
				HashMap<String, String[]> commandList = listener.moduleInfo.getCommands();
				for (Entry<String, String[]> commandInfo : commandList.entrySet())
				{
					if (!commandInfo.getKey().isEmpty())
						commands += commandInfo.getKey() + " ";
				}
			}
		}
		String[] commandHelp = { "Commands: " + commands, "For command specific help, type \"-help\" or \"-h\" after a command." };
		return commandHelp;
	}
}