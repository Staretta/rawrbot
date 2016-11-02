package net.staretta.modules;

import java.util.ArrayList;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.Command;
import net.staretta.businesslogic.ModuleInfo;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class About extends BaseListener
{
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("About");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.0");
		moduleInfo.addCommand(new Command("!about", "!about : About the bot"));
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		if (isCommand(event.getMessage(), "!about"))
		{
			ArrayList<String> about = new ArrayList<String>();
			about.add(event.getBot().getNick() + " was created by Staretta. Programmed in Java, using the PircBotX Library. ");
			about.add("Want a new feature? Message Staretta on IRC or send her an email at requests@staretta.com");
			for (String message : about)
				event.getChannel().send().message(message);
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (isCommand(event.getMessage(), "!about"))
		{
			ArrayList<String> about = new ArrayList<String>();
			about.add(event.getBot().getNick() + " was created by Staretta. Programmed in Java, using the PircBotX Library. ");
			about.add("Want a new feature? Message Staretta on IRC or send her an email at requests@staretta.com");
			for (String message : about)
				event.getUser().send().message(message);
		}
	}
}
