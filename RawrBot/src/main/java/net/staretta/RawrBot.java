package net.staretta;

import java.util.List;
import java.util.Set;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.entity.ChannelEntity;
import net.staretta.businesslogic.entity.GlobalConfigEntity;
import net.staretta.businesslogic.entity.ServerEntity;
import net.staretta.businesslogic.services.EmailService;
import net.staretta.businesslogic.services.EmailService.EmailCredentials;
import net.staretta.businesslogic.services.ServerService;

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
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.google.common.collect.ImmutableSortedSet;

public class RawrBot
{
	private static ApplicationContext applicationContext;
	
	public static void main(String[] args)
	{
		Logger logger = LoggerFactory.getLogger(RawrBot.class);
		
		logger.info("Initializing Spring context.");
		applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
		logger.info("Spring context initialized.");
		
		logger.info("Loading bot settings.");
		ServerService serverService = getAppCtx().getBean(ServerService.class);
		List<ServerEntity> serverSettings = serverService.getBotSettings();
		logger.info("Loaded bot settings from database.");
		
		// TODO: Specific smtp servers for different servers.
		logger.info("Configuring global email settings");
		EmailService emailService = getAppCtx().getBean(EmailService.class);
		emailService.setGlobalCredentials(getGlobalEmailCredentials(serverService));
		
		MultiBotManager manager = new MultiBotManager();
		for (ServerEntity server : serverSettings)
		{
			logger.info("Building bot for server: " + server.getServer());
			// @formatter:off
			Builder builder = new Configuration.Builder()
				.setName(server.getNickname())
				.setLogin(server.getUsername())
				.setRealName(server.getVersion())
				.setAutoNickChange(true)
				.setAutoReconnect(true)
				.setCapEnabled(true)
				.setIdentServerEnabled(false)
				.addServer(server.getServer(), server.getPort())
				.setNickservPassword(server.getPassword())
				.setAutoReconnect(true)
				.setAutoReconnectAttempts(Integer.MAX_VALUE);
			// @formatter:on
			
			// We have to trust all certificates because some IRC servers are stupid and use unsigned certs.
			if (server.isSsl())
				builder.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates());
			
			for (ChannelEntity channel : server.getChannels())
			{
				if (channel.hasPassword())
					builder.addAutoJoinChannel(channel.getChannel(), channel.getPassword());
				else
					builder.addAutoJoinChannel(channel.getChannel());
			}
			
			// Find all the modules in net.staretta, and we'll sort out whether they should be used in BaseListener
			final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
			// provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
			provider.addIncludeFilter(new AssignableTypeFilter(BaseListener.class));
			final Set<BeanDefinition> classes = provider.findCandidateComponents("net.staretta");
			
			for (BeanDefinition module : classes)
			{
				try
				{
					builder.addListener((Listener) Class.forName(module.getBeanClassName()).newInstance());
					logger.info("Added module: " + module.getBeanClassName());
				}
				catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
				{
					logger.error("Exception in RawrBot.main: ", e);
				}
			}
			
			Configuration config = builder.buildConfiguration();
			manager.addBot(config);
			logger.info("IRC bot built and added to manager: " + server.getServer());
		}
		logger.info("Starting IRC bots.");
		manager.start();
		
		/****************
		 * Bot monitoring
		 * 
		 * The bots throw an exception when they get disconnected from a server, and never reconnect.
		 * This is a retarded stopgap measure at best, sometimes it works, sometimes it doesn't.
		 */
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
				String server = bot.getServerHostname();
				String nickname = bot.getNick();
				logger.info("Bot Monitor Check -> Checking Bot: " + nickname + "@" + server);
				if (!bot.isConnected() || bot.getState().equals(PircBotX.State.DISCONNECTED))
				{
					logger.info("Bot Monitor Check -> Bot Offline: " + nickname + "@" + server);
					manager.addBot(bot);
					logger.info("Bot Monitor Check -> Restarting Bot: " + nickname + "@" + server);
				}
			}
		}
	}
	
	public static EmailCredentials getGlobalEmailCredentials(ServerService serverService)
	{
		GlobalConfigEntity config = null;
		ServerEntity server = serverService.getBotSettings().get(0);
		EmailCredentials credentials = new EmailCredentials();
		if (server.getGlobalConfig() != null)
		{
			config = server.getGlobalConfig();
			credentials.smtpHost = config.getSmtpHost();
			credentials.username = config.getSmtpUsername();
			credentials.password = config.getSmtpPassword();
			credentials.authenticate = config.isSmtpAuth();
			credentials.ttls = config.isSmtpSsl();
			credentials.port = config.getSmtpPort();
		}
		return credentials;
	}
	
	public static ApplicationContext getAppCtx()
	{
		return applicationContext;
	}
}
