package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.services.SexDiceService;
import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SexDice extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private SexDiceService service;
	
	public SexDice()
	{
		service = RawrBot.applicationContext.getBean(SexDiceService.class);
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("SexDice");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.1");
		moduleInfo.addCommand("!sexdice", "!sexdice: Displays an action and a location for various erotic fantasies. "
				+ "You can also specify an !action !bodypart or !location seperately.");
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		if (ircUtil.isCommand(event, "!sexdice"))
		{
			ircUtil.sendMessage(event, service.getRandomSexDice());
		}
		else if (ircUtil.isCommand(event, "!action"))
		{
			ircUtil.sendMessage(event, service.getRandomAction());
		}
		else if (ircUtil.isCommand(event, "!bodypart"))
		{
			ircUtil.sendMessage(event, service.getRandomBodypart());
		}
		else if (ircUtil.isCommand(event, "!location"))
		{
			ircUtil.sendMessage(event, service.getRandomLocation());
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
	}
}