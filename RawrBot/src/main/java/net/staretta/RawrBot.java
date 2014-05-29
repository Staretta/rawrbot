package net.staretta;

import net.staretta.util.ircUtil;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RawrBot extends ListenerAdapter implements Listener
{
	// slf4j Stuff
	private static Logger logger = LoggerFactory.getLogger(RawrBot.class);
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{
		if ((event.getUser().getNick().equalsIgnoreCase(bot_owner) || ircUtil.isOP(event, irc_channel))
				&& event.getMessage().equalsIgnoreCase("!quit"))
		{
			// Shutdown upon receiving a quit command
			ircUtil.sendMessage(event, "Shutting Down...");
			event.getBot().stopBotReconnect();
			event.getBot().sendIRC().quitServer();
		}
		else if (event.getUser().getNick().equalsIgnoreCase(bot_owner) && event.getMessage().equalsIgnoreCase("!join"))
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
		if (event.getUser().getNick().equalsIgnoreCase(irc_nickname))
		{
			event.getBot().sendIRC().changeNick(irc_nickname);
		}
	}
	
	@Override
	public void onConnect(ConnectEvent event) throws Exception
	{
		if (!bot_password.isEmpty())
		{
			// TODO: replace with identify at some point.
			logger.info("(" + event.getBot().getNick() + "->NickServ) IDENTIFY " + "PASSWORD_HERE");
			event.getBot().sendIRC().message("NickServ", "IDENTIFY " + bot_password);
		}
	}
	
	@Override
	public void onNickAlreadyInUse(NickAlreadyInUseEvent event)
	{
		event.respond(alterCollidedNick(event.getUsedNick()));
	}
	
	private String alterCollidedNick(String nickname)
	{
		// If there is already a nickname with our name on the server, then we need to change our name so it'll work.
		if (nickname.contains("Rawr"))
		{
			nickname.replace("Rawr", "Rawrr");
			return nickname;
		}
		else
		{
			return nickname + "_";
		}
	}
	
	@Override
	public void onUnknown(UnknownEvent event) throws Exception
	{
		logger.info(event.toString());
	}
	
	public static void main(String[] args)
	{
		logger.info("Initializing Spring context.");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
		logger.info("Spring context initialized.");
		
		// @formatter:off
        // Configuration
        Configuration configuration = new Configuration.Builder()
                .setName(cfg.getProperty("irc_nickname"))
                .setLogin(cfg.getProperty("irc_username"))
                .setRealName(cfg.getProperty("bot_version"))
                .setAutoNickChange(true)
                .setAutoReconnect(true)
                .setCapEnabled(true)
                .setIdentServerEnabled(false)
                .setServerHostname(cfg.getProperty("irc_server"))
                .setServerPort(Integer.parseInt(cfg.getProperty("irc_port")))
                .addAutoJoinChannel(cfg.getProperty("irc_channel"))
                //.setNickservPassword(bot_password) // REMEMER TO UNCOMMENT THIS BEFORE PUSHING UPDATE
                //.addListener(new Dice())
                .buildConfiguration();
        PircBotX bot = new PircBotX(configuration);
        // @formatter:on
		
		try
		{
			bot.startBot();
		}
		catch (Exception ex)
		{
			
			logger.error(ex.getCause().toString());
		}
	}
}
