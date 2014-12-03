package net.staretta.modules;

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
	
	private String about = "Created by Staretta. Programmed in Java, using the PircBotX Library. "
			+ "Want a new feature? Message Staretta on IRC or send her an email at requests@staretta.net";
	
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
			ircUtil.sendMessage(event, about);
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (ircUtil.isCommand(event, "!about"))
			ircUtil.sendPrivateMessage(event, about);
	}
	
}
