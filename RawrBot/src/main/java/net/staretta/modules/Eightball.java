package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.services.EightballService;
import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Eightball extends BaseListener
{
	private static Logger logger = LoggerFactory.getLogger(Eightball.class);
	private EightballService eightballService;
	
	public Eightball()
	{
		eightballService = RawrBot.applicationContext.getBean(EightballService.class);
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Eightball");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.1");
		moduleInfo.addCommandInfo("!eightball",
				"!eightball <question>?: Mystically queries the magic eightball for an answer to your question."
						+ " Note: Question must end with a question mark. \"?\"");
		moduleInfo.addCommandInfo("!8ball", "!8ball <question>?: Mystically queries the magic eightball for an answer to your question."
				+ " Note: Question must end with a question mark. \"?\"");
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		if ((ircUtil.isCommand(event, "!8ball") || ircUtil.isCommand(event, "!eightball") || ircUtil.isCommand(event, "!8-ball"))
				&& event.getMessage().trim().toLowerCase().endsWith("?") && !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			String answer = eightballService.getRandomAnswer();
			ircUtil.sendMessage(event, answer);
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if ((ircUtil.isCommand(event, "!8ball") || ircUtil.isCommand(event, "!eightball") || ircUtil.isCommand(event, "!8-ball"))
				&& event.getMessage().trim().toLowerCase().endsWith("?"))
		{
			String answer = eightballService.getRandomAnswer();
			ircUtil.sendMessage(event, answer);
		}
	}
	
}