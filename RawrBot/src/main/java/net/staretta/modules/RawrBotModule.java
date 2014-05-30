package net.staretta.modules;

import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawrBotModule extends ListenerAdapter
{
	private Logger	logger	= LoggerFactory.getLogger(RawrBotModule.class);
	private String	botOwner;
	private String	ircChannel;
	private String	botNickname;
	private String	botPassword;

	@Override
	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{
		if ((event.getUser().getNick().equalsIgnoreCase(botOwner) || ircUtil.isOP(event, ircChannel))
				&& event.getMessage().equalsIgnoreCase("!quit"))
		{
			// Shutdown upon receiving a quit command
			ircUtil.sendMessage(event, "Shutting Down...");
			event.getBot().stopBotReconnect();
			event.getBot().sendIRC().quitServer();
		}
		else if (event.getUser().getNick().equalsIgnoreCase(botOwner) && event.getMessage().equalsIgnoreCase("!join"))
		{
			String[] param = event.getMessage().trim().split("\\s", 3);

			if (param.length != 1)
				event.getBot().sendIRC().joinChannel(param[1]);
		}
	}

	@Override
	public void onQuit(QuitEvent event) throws Exception
	{
		// If we see our default nickname quit, then rename our name to it.

		if (event.getUser().getNick().equalsIgnoreCase(botNickname))
		{
			event.getBot().sendIRC().changeNick(botNickname);
		}
	}

	@Override
	public void onUnknown(UnknownEvent event) throws Exception
	{
		logger.debug("UNKNOWN EVENT: " + event.toString());
	}
}