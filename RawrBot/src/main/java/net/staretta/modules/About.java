package net.staretta.modules;

import java.util.ArrayList;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.util.ircUtil;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class About extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("About");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.0");
		moduleInfo.addCommand("!about", "!about : About the bot");
		return moduleInfo;
	}

	@Override
	public void OnMessage(MessageEvent event)
	{
		if (ircUtil.isCommand(event, "!about"))
		{
			ArrayList<String> about = new ArrayList<String>();
			about.add(event.getBot().getNick()
					+ " was created by Staretta. Programmed in Java, using the PircBotX Library. ");
			about.add("Want a new feature? Message Staretta on IRC or send her an email at requests@staretta.net");
			for (String message : about)
				event.getChannel().send().message(message);
		}
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (ircUtil.isCommand(event, "!about"))
		{
			ArrayList<String> about = new ArrayList<String>();
			about.add(event.getBot().getNick()
					+ " was created by Staretta. Programmed in Java, using the PircBotX Library. ");
			about.add("Want a new feature? Message Staretta on IRC or send her an email at requests@staretta.net");
			for (String message : about)
				event.getUser().send().message(message);
		}
	}
}
