package net.staretta.modules;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class Help extends BaseListener
{
	private static Logger	logger	= LoggerFactory.getLogger(Help.class);

	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v2.0");
		moduleInfo.setHelpCommand("!help !commands");
		moduleInfo.setName("Help");
		return moduleInfo;
	}

	@Override
	public void OnMessage(MessageEvent event)
	{
		if ((ircUtil.isCommand(event, "!commands") || ircUtil.isCommand(event, "!help"))
				&& !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			ImmutableSet<Listener<PircBotX>> listeners = event.getBot().getConfiguration().getListenerManager()
					.getListeners();
			for (String line : helpCommand(listeners))
				ircUtil.sendMessage(event, line);
		}
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (ircUtil.isCommand(event, "!commands") || ircUtil.isCommand(event, "!help"))
		{
			ImmutableSet<Listener<PircBotX>> listeners = event.getBot().getConfiguration().getListenerManager()
					.getListeners();
			for (String line : helpCommand(listeners))
				ircUtil.sendMessage(event, line);
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
				listener.moduleInfo.getHelpCommand();
				if (!listener.moduleInfo.getHelpCommand().isEmpty())
					commands += listener.moduleInfo.getHelpCommand() + " ";
			}
		}
		String[] commandHelp = { "Commands: " + commands, "For command specific help, type \"-help\" after a command." };
		return commandHelp;
	}
}