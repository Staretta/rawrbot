package net.staretta.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if ((isCommand(event.getMessage(), "!commands") || isCommand(event.getMessage(), "!help"))
				&& !RateLimiter.isRateLimited(event.getUser()))
		{
			// Get a list of modules available to the channel.
			List<String> modules = settingsService.getChannelModules(event.getBot().getConfiguration().getServerHostname(), event
					.getChannel().getName());
			
			if (modules == null)
			{
				return;
			}
			
			// Create a temporary list to store the listeners.
			List<Listener<PircBotX>> temp = new ArrayList<Listener<PircBotX>>();
			
			// Loop through the available modules currently in use by the server and if they match the modules allowed
			// for the channel, then add the listener to the temporary list.
			for (Listener<PircBotX> listener : event.getBot().getConfiguration().getListenerManager().getListeners())
			{
				String[] name = listener.getClass().getName().split("\\.");
				
				if (modules.contains(name[name.length - 1]))
				{
					temp.add(listener);
				}
			}
			
			// The helpCommand function expects an ImmutableSet, so we copy the temp list to an ImmutableSet so the
			// function can use it.
			for (String message : helpCommand(ImmutableSet.copyOf(temp.iterator())))
				event.getChannel().send().message(message);
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
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
				
				HashMap<String, List<String>> commandList = listener.getModuleInfo().getCommands();
				for (Entry<String, List<String>> commandInfo : commandList.entrySet())
				{
					if (!commandInfo.getKey().isEmpty())
						commands += commandInfo.getKey() + " ";
				}
			}
		}
		String[] commandHelp = { "Commands: " + commands, "For command specific help, type \"--help\" or \"-h\" after a command." };
		return commandHelp;
	}
}