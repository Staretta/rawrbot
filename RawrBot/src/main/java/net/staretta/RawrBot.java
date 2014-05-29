package net.staretta;

import java.io.IOException;
import java.util.List;

import net.staretta.businesslogic.entity.Settings;
import net.staretta.businesslogic.services.SettingsService;
import net.staretta.util.ircUtil;

import org.pircbotx.Configuration;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
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
	private static SettingsService settingsService;
	private String botOwner;
	private String ircChannel;
	private String botNickname;
	private String botPassword;
	
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
	public void onConnect(ConnectEvent event) throws Exception
	{
		if (!botPassword.isEmpty())
		{
			// TODO: replace with identify at some point.
			logger.info("(" + event.getBot().getNick() + "->NickServ) IDENTIFY " + "PASSWORD_HERE");
			event.getBot().sendIRC().message("NickServ", "IDENTIFY " + botPassword);
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
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException,
			IrcException
	{
		logger.info("Initializing Spring context.");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
		logger.info("Spring context initialized.");
		
		SettingsService settingsService = (SettingsService) applicationContext.getBean(SettingsService.class);
		List<Settings> settings = settingsService.getBotSettings();
		
		PircBotX bot;
		for (Settings s : settings)
		{
			// @formatter:off
			Builder builder = new Configuration.Builder()
				.setName(s.getNickname())
				.setLogin(s.getUsername())
				.setRealName(s.getVersion())
				.setAutoNickChange(true)
				.setAutoReconnect(true)
				.setCapEnabled(true)
				.setIdentServerEnabled(false)
				.setServerHostname(s.getServer())
				.setServerPort(s.getPort())
				.setNickservPassword(s.getPassword());
			// @formatter:on
			for (String channel : s.getChannels())
				builder.addAutoJoinChannel(channel);
			for (String module : s.getModules())
				builder.addListener((Listener) Class.forName(module).newInstance());
			Configuration config = builder.buildConfiguration();
			bot = new PircBotX(config);
			bot.startBot();
		}
	}
}
