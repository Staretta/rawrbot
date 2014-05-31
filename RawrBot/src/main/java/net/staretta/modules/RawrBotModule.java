package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.services.SettingsService;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawrBotModule extends ListenerAdapter
{
	private Logger			logger		= LoggerFactory.getLogger(RawrBotModule.class);
	private SettingsService	settingsService;
	public static String	help		= "";
	public static String	helpCommand	= "";

	public RawrBotModule()
	{
		settingsService = RawrBot.applicationContext.getBean(SettingsService.class);
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{
		logger.info(event.getBot().getConfiguration().getServerHostname());
		// if ((event.getUser().getNick().equalsIgnoreCase(botOwner) || ircUtil.isOP(event, ircChannel))
		// && event.getMessage().equalsIgnoreCase("!quit"))
		// {
		// // Shutdown upon receiving a quit command
		// ircUtil.sendMessage(event, "Shutting Down...");
		// event.getBot().stopBotReconnect();
		// event.getBot().sendIRC().quitServer();
		// }
		//
		// if (event.getUser().getNick().equalsIgnoreCase(botOwner) && event.getMessage().equalsIgnoreCase("!join"))
		// {
		// String[] param = event.getMessage().trim().split("\\s", 3);
		//
		// if (param.length != 1)
		// event.getBot().sendIRC().joinChannel(param[1]);
		// }
	}

	@Override
	public void onQuit(QuitEvent event) throws Exception
	{
		// If we see our default nickname quit, then rename our name to it.
		if (event.getUser().getNick().equalsIgnoreCase(event.getBot().getConfiguration().getName()))
		{
			event.getBot().sendIRC().changeNick(event.getBot().getConfiguration().getName());
		}
	}

	@Override
	public void onUnknown(UnknownEvent event) throws Exception
	{
		logger.debug("UNKNOWN EVENT: " + event.toString());
	}
}