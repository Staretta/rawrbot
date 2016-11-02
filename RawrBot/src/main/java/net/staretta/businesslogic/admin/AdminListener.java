package net.staretta.businesslogic.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.Option;
import net.staretta.businesslogic.services.UserService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public abstract class AdminListener extends BaseListener
{
	private AdminInfo adminInfo;
	private UserService userService;
	
	public AdminListener()
	{
		adminInfo = setAdminInfo();
		userService = RawrBot.getAppCtx().getBean(UserService.class);
	}
	
	public abstract AdminInfo setAdminInfo();
	
	public AdminInfo getAdminInfo()
	{
		return adminInfo;
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		return moduleInfo;
	};
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		// We don't care about MessageEvents. We only care about PrivateMessageEvents
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		// We check to see if we have an !admin command
		String m = event.getMessage();
		if (isCommand(m, "!admin"))
		{
			// Break apart their message, and get a list of options available for !admin command. Then check to see if their option is in
			// our list.
			List<String> params = Arrays.asList(m.trim().split("\\s"));
			ArrayList<Option> optionList = adminInfo.getOptions();
			if (params.size() > 1 && (optionList.contains(params.get(1).toLowerCase()) && isHelp(m)))
			{
				// Grab the option help information from the list, and send it to the user.
				ArrayList<String> options = optionList.stream().filter(o -> o.getOption().equalsIgnoreCase(params.get(1))).findFirst()
						.get().getOptionHelp();
				for (String message : options)
				{
					event.getUser().send().message(message);
				}
			}
			else
			{
				OnAdminPrivateMessage(event);
			}
		}
	}
	
	public abstract void OnAdminPrivateMessage(PrivateMessageEvent event);
	
	public UserService getUserService()
	{
		return userService;
	}
}
