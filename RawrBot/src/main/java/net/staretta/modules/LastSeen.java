package net.staretta.modules;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.entity.LastSeenEntity;
import net.staretta.businesslogic.entity.LastSeenEntity.MessageType;
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

	// TODO: Set and filter by channel
	public LastSeen()
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
		if (!event.getUser().getNick().equals(event.getBot().getNick())) // Need to make sure not our bot.
		{
			LastSeenEntity seen = service.getLastSeen(event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());

			if (seen != null)
			{
				seen.setUsername(event.getUser().getLogin());
				seen.setHostmask(event.getUser().getHostmask());
				seen.setDate(new Date());
				seen.setMessage(event.getMessage());
				seen.setMessageType(MessageType.ACTION);
				service.updateLastSeen(seen);
			}
			else
			{
				service.addLastSeen(event.getUser(), event.getMessage(), event.getBot().getConfiguration().getServerHostname(), event
						.getChannel().getName(), MessageType.MESSAGE);
			}
		}
	}

	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if (!event.getUser().getNick().equals(event.getBot().getNick())) // Need to make sure not our bot.
		{
			LastSeenEntity seen = service.getLastSeen(event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());

			if (seen != null)
			{
				seen.setUsername(event.getUser().getLogin());
				seen.setHostmask(event.getUser().getHostmask());
				seen.setDate(new Date());
				seen.setMessage(event.getMessage());
				seen.setMessageType(MessageType.MESSAGE);
				service.updateLastSeen(seen);
			}
			else
			{
				service.addLastSeen(event.getUser(), event.getMessage(), event.getBot().getConfiguration().getServerHostname(), event
						.getChannel().getName(), MessageType.MESSAGE);
			}

			if (isCommand(event.getMessage(), "!seen") || isCommand(event.getMessage(), "!lastseen"))
			{
				String[] params = event.getMessage().trim().split("\\s");
				if (params.length > 1)
				{
					LastSeenEntity lastSeen = service.getLastSeen(params[1], event.getBot().getConfiguration().getServerHostname());
					if (lastSeen != null)
					{
						String message = params[1] + " last seen " + new SimpleDateFormat("MM/dd/yy HH:mm:ss z").format(lastSeen.getDate());
						event.getChannel().send().message(message);
					}
					else
					{
						event.getChannel().send().message(params[1] + " hasn't said anything.");
					}
				}
			}
		}
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		if (isCommand(event.getMessage(), "!seen") || isCommand(event.getMessage(), "!lastseen"))
		{
			String[] params = event.getMessage().trim().split("\\s");
			if (params.length > 1)
			{
				LastSeenEntity lastSeen = service.getLastSeen(params[1], event.getBot().getConfiguration().getServerHostname());
				if (lastSeen != null)
				{
					String message = params[1] + " last seen " + new SimpleDateFormat("MM/dd/yy HH:mm:ss z").format(lastSeen.getDate());
					event.getUser().send().message(message);
				}
				else
				{
					event.getUser().send().message(params[1] + " hasn't said anything.");
				}
			}
		}
	}
}
