package net.staretta;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.staretta.businesslogic.entity.ChannelEntity;
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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

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
		logger.info("Loaded bot settings from database.");

		MultiBotManager<PircBotX> manager = new MultiBotManager<PircBotX>();
		for (Settings setting : serverSettings)
		{
			logger.info("Building bot for server: " + setting.getServer());
			// @formatter:off
			Builder<PircBotX> builder = new Configuration.Builder<PircBotX>()
				.setName(setting.getNickname())
				.setLogin(setting.getUsername())
				.setRealName(setting.getVersion())
				.setAutoNickChange(true)
				.setAutoReconnect(true)
				.setCapEnabled(true)
				.setIdentServerEnabled(false)
				.setServerHostname(setting.getServer())
				.setServerPort(setting.getPort())
				.setNickservPassword(setting.getPassword())
				.setAutoReconnect(true);
			// @formatter:on

			if (setting.isSsl())
				builder.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates());

			for (ChannelEntity channel : setting.getChannels())
				builder.addAutoJoinChannel(channel.getChannel());

			// Find all the modules in net.staretta.modules, and we'll sort out whether they should be used in BaseListener
			final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
			provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
			final Set<BeanDefinition> classes = provider.findCandidateComponents("net.staretta.modules");

			for (BeanDefinition module : classes)
			{
				try
				{
					builder.addListener((Listener<PircBotX>) Class.forName(module.getBeanClassName()).newInstance());
					logger.info("Added module: " + module.getBeanClassName());
				}
				catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
				{
					logger.error("Exception in RawrBot.main: ", e);
				}
			}

			Configuration<PircBotX> config = builder.buildConfiguration();
			manager.addBot(config);
			logger.info("IRC bot built and added to manager: " + setting.getServer());
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