package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.services.MessageLogService;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpamAlert extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogService service;

	public SpamAlert()
	{
		service = RawrBot.applicationContext.getBean(MessageLogService.class);
	}

	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("SpamAlert");
		return moduleInfo;
	}

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception
	{
		if (service.isSpam(event.getUser(), event.getMessage(), event.getBot().getConfiguration().getServerHostname(), 3, 2))
		{
			for (User user : event.getChannel().getOps())
			{
				String message = "[SPAM ALERT] Channel: " + event.getChannel().getName() + " User: " + event.getUser().getNick()
						+ " Message: " + event.getMessage();
				event.getBot().sendIRC().message(user.getNick(), message);
			}
		}
	}

	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		// Do nothing
	}
}
