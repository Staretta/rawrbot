package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.Command;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.services.EightballService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class Eightball extends BaseListener
{
	private EightballService service;
	
	public Eightball()
	{
		service = RawrBot.getAppCtx().getBean(EightballService.class);
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Eightball");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.1");
		moduleInfo.addCommand(new Command("!eightball",
				"!eightball <question>?: Mystically queries the magic eightball for an answer to your question."
						+ " Note: Question must end with a question mark. \"?\""));
		moduleInfo.addCommand(new Command("!8ball",
				"!8ball <question>?: Mystically queries the magic eightball for an answer to your question."
						+ " Note: Question must end with a question mark. \"?\""));
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if ((isCommand(event.getMessage(), "!8ball") || isCommand(event.getMessage(), "!eightball") || isCommand(event.getMessage(),
				"!8-ball")) && event.getMessage().trim().toLowerCase().endsWith("?") && !RateLimiter.isRateLimited(event.getUser()))
		{
			String answer = service.getRandomAnswer();
			event.getChannel().send().message(answer);
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		if ((isCommand(event.getMessage(), "!8ball") || isCommand(event.getMessage(), "!eightball") || isCommand(event.getMessage(),
				"!8-ball")) && event.getMessage().trim().toLowerCase().endsWith("?"))
		{
			String answer = service.getRandomAnswer();
			event.getUser().send().message(answer);
		}
	}
}