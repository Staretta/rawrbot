package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.services.SexDiceService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class SexDice extends BaseListener
{
	private SexDiceService service;
	
	public SexDice()
	{
		service = RawrBot.getAppCtx().getBean(SexDiceService.class);
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
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if ((isCommand(event.getMessage(), "!sexdice") || isCommand(event.getMessage(), "!action")
				|| isCommand(event.getMessage(), "!bodypart") || isCommand(event.getMessage(), "!location"))
				&& !RateLimiter.isRateLimited(event.getUser()))
			if (isCommand(event.getMessage(), "!sexdice"))
			{
				event.getChannel().send().message(service.getRandomSexDice());
			}
			else if (isCommand(event.getMessage(), "!action"))
			{
				event.getChannel().send().message(service.getRandomAction());
			}
			else if (isCommand(event.getMessage(), "!bodypart"))
			{
				event.getChannel().send().message(service.getRandomBodypart());
			}
			else if (isCommand(event.getMessage(), "!location"))
			{
				event.getChannel().send().message(service.getRandomLocation());
			}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
	}
}