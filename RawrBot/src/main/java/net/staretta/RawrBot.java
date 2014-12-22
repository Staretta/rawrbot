package net.staretta;

import java.util.List;

import net.staretta.businesslogic.entity.Settings;
import net.staretta.businesslogic.services.SettingsService;

import org.pircbotx.Configuration;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.hooks.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.ImmutableSortedSet;

public class RawrBot
{
	public static ApplicationContext applicationContext;
	
	public static void main(String[] args)
	{
		Logger logger = LoggerFactory.getLogger(RawrBot.class);
		
		logger.info("Initializing Spring context.");
		applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
		logger.info("Spring context initialized.");
		
		SettingsService settingsService = applicationContext.getBean(SettingsService.class);
		List<Settings> serverSettings = settingsService.getBotSettings();
		logger.info("Bot Settings Loaded.");
		
		MultiBotManager<PircBotX> manager = new MultiBotManager<PircBotX>();
		for (Settings setting : serverSettings)
		{
			// @formatter:off
			Builder<PircBotX> builder = new Configuration.Builder<PircBotX>()
				.setName(setting.getNickname())
				.setLogin(setting.getUsername())
				.setRealName(setting.getVersion())
				.setAutoNickChange(true)
				.setAutoReconnect(true)
				.setCapEnabled(true)
				//.addCapHandler(new EnableCapHandler("identify-msg", true))
				//.addCapHandler(new EnableCapHandler("account-notify", true))
				.setIdentServerEnabled(false)
				.setServerHostname(setting.getServer())
				.setServerPort(setting.getPort())
				.setNickservPassword(setting.getPassword())
				.setAutoReconnect(true);
			// @formatter:on
			
			if (setting.isSsl())
				builder.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates());
			
			for (String channel : setting.getChannels())
				builder.addAutoJoinChannel(channel);
			
			for (String module : setting.getModules())
			{
				try
				{
					builder.addListener((Listener<PircBotX>) Class.forName("net.staretta.modules." + module).newInstance());
				}
				catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
				{
					logger.error("Exception in RawrBot.main: ", e);
				}
			}
			
			Configuration<PircBotX> config = builder.buildConfiguration();
			manager.addBot(config);
			logger.info("Added IRC bot to manager: " + setting.getServer());
		}
		logger.info("Starting IRC bots.");
		manager.start();
		
		// Bot monitoring
		// The bots throw an exception when they get disconnected from a server, and never reconnect.
		// This is a stopgap measure at best, sometimes it works, sometimes it doesn't.
		ImmutableSortedSet<PircBotX> bots = manager.getBots();
		while (true)
		{
			try
			{
				Thread.sleep(300000);
			}
			catch (InterruptedException e)
			{
				logger.info("Exception: " + e);
			}
			logger.info("Bot Monitor Check");
			for (PircBotX bot : bots)
			{
				logger.info("Bot Monitor Check -> Checking Bot: " + bot.getNick() + "@" + bot.getConfiguration().getServerHostname());
				if (!bot.isConnected() || bot.getState().equals(PircBotX.State.DISCONNECTED))
				{
					logger.info("Bot Monitor Check -> Bot Offline: " + bot.getNick() + "@" + bot.getConfiguration().getServerHostname());
					manager.addBot(bot);
					logger.info("Bot Monitor Check -> Restarting Bot: " + bot.getNick() + "@" + bot.getConfiguration().getServerHostname());
				}
			}
		}
	}
}