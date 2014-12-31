package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.entity.LastSeenEntity;
import net.staretta.businesslogic.services.LastSeenService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastSeen extends BaseListener
{
	private LastSeenService service;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private LastSeen()
	{
		service = RawrBot.applicationContext.getBean(LastSeenService.class);
	}

	@Override
	protected ModuleInfo setModuleInfo()
	{
		moduleInfo = new ModuleInfo();
		moduleInfo.setName("LastSeen");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.0");
		moduleInfo.addCommand("!seen",
				"!seen <Nickname> : Displays the last thing a user said, based on their nickname, and when the user was last seen.");
		moduleInfo.addCommand("!lastseen",
				"!lastseen <Nickname> : Displays the last thing a user said, based on their nickname, and when the user was last seen.");
		return moduleInfo;
	}

	@Override
	public void onAction(ActionEvent<PircBotX> event) throws Exception
	{

	}

	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		LastSeenEntity seen = service.getLastSeen(event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{

	}
}
