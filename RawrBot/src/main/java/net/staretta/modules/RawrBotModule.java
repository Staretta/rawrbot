package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.services.SettingsService;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawrBotModule extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private SettingsService settingsService;
	
	public RawrBotModule()
	{
		settingsService = RawrBot.applicationContext.getBean(SettingsService.class);
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("RawrBotModule");
		moduleInfo.setAuthor("Staretta");
		return moduleInfo;
	}
	
	@Override
	public void onQuit(QuitEvent event)
	{
		// If we see our default nickname quit, then rename our name to it.
		if (event.getUser().getNick().equalsIgnoreCase(event.getBot().getConfiguration().getName()))
			if (event.getBot().isConnected())
				event.getBot().sendIRC().changeNick(event.getBot().getConfiguration().getName());
	}
	
	@Override
	public void onUnknown(UnknownEvent event)
	{
		logger.debug("UNKNOWN EVENT: " + event.toString());
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		// logger.info(event.getBot().getConfiguration().getServerHostname());
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
}