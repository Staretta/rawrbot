package net.staretta.modules;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class Tea extends BaseListener
{

	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Tea");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.0");
		moduleInfo.addCommand("!tea", "!tea [user] : Gives you, or the user, some tea. [Command idea by GothicEmily]");
		return moduleInfo;
	}

	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if (isCommand(event.getMessage(), "!tea") && !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			String[] param = event.getMessage().trim().split("\\s");
			if (param.length == 1)
			{
				event.getChannel().send()
						.action("pours " + event.getUser().getNick() + " a liquid almost, but not quite, entirely unlike tea.");
			}
			else if (param.length == 2)
			{
				for (User user : event.getChannel().getUsers())
				{
					if (param[1].toLowerCase().equals(user.getNick().toLowerCase()))
					{
						event.getChannel().send()
								.action("pours " + user.getNick() + " a liquid almost, but not quite, entirely unlike tea.");
					}
				}
			}
		}
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
	}
}
