package net.staretta.modules;

import java.util.ArrayList;
import java.util.Arrays;

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
			event.getChannel().send().message(service.getRandomSexDice());
		}
		else if (ircUtil.isCommand(event, "!action"))
		{
			event.getChannel().send().message(service.getRandomAction());
		}
		else if (ircUtil.isCommand(event, "!bodypart"))
		{
			event.getChannel().send().message(service.getRandomBodypart());
		}
		else if (ircUtil.isCommand(event, "!location"))
		{
			event.getChannel().send().message(service.getRandomLocation());
		}
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (ircUtil.isCommand(event, "!sexdice"))
		{
			ArrayList<String> params = new ArrayList<String>(Arrays.asList(event.getMessage().trim().split("\\s")));
			if (params.size() >= 3 && event.getUser().getNick().equals("Staretta"))
			{
				StringBuilder sb = new StringBuilder();
				for (int i = 2; i < params.size(); i++)
					sb.append(params.get(i) + " ");
				if (params.get(1).equals("-addAction") || params.get(1).equals("-aA"))
					service.addAction(sb.toString().trim());
				else if (params.get(1).equals("-addBodypart") || params.get(1).equals("-aB"))
					service.addBodypart(sb.toString().trim());
				else if (params.get(1).equals("-addLocation") || params.get(1).equals("-aL"))
					service.addLocation(sb.toString().trim());
				event.getUser().send().message("Successfully added sexdice.");
			}
		}
	}
}