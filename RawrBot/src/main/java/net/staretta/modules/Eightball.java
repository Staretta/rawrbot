package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.services.EightballService;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Eightball extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private EightballService service;

	public Eightball()
	{
		service = RawrBot.applicationContext.getBean(EightballService.class);
	}

	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Eightball");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.1");
		moduleInfo.addCommand("!eightball",
				"!eightball <question>?: Mystically queries the magic eightball for an answer to your question."
						+ " Note: Question must end with a question mark. \"?\"");
		moduleInfo.addCommand("!8ball", "!8ball <question>?: Mystically queries the magic eightball for an answer to your question."
				+ " Note: Question must end with a question mark. \"?\"");
		return moduleInfo;
	}

	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if ((isCommand(event.getMessage(), "!8ball") || isCommand(event.getMessage(), "!eightball") || isCommand(event.getMessage(),
				"!8-ball"))
				&& event.getMessage().trim().toLowerCase().endsWith("?")
				&& !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			String answer = service.getRandomAnswer();
			event.getChannel().send().message(answer);
		}
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		if (isCommand(event.getMessage(), "!8ball") || isCommand(event.getMessage(), "!eightball")
				|| isCommand(event.getMessage(), "!8-ball"))
		{
			String[] params = event.getMessage().trim().split("\\s");

			if (params.length >= 3 && (params[1].equals("-add") || params[1].equals("-a")) && event.getUser().getNick().equals("Staretta"))
			{
				logger.info(StringUtils.join(params, " ", 2, params.length));
				service.addAnswer(StringUtils.join(params, " ", 2, params.length));
				event.getUser().send().message("Successfully added new 8ball answer.");
			}
			else if (event.getMessage().trim().toLowerCase().endsWith("?"))
			{
				String answer = service.getRandomAnswer();
				event.getUser().send().message(answer);
			}
		}
	}
}