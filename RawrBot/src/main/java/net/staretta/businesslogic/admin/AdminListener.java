package net.staretta.businesslogic.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
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
		
		// optionParser.acceptsAll(Arrays.asList("i", "identify"));
		// optionParser.acceptsAll(Arrays.asList("r", "register"));
		// optionParser.acceptsAll(Arrays.asList("p", "pass", "password")).requiredIf("identify", "register").requiresArgument();
		// optionParser.acceptsAll(Arrays.asList("e", "email")).requiredIf("register").requiresArgument();
		// optionParser.acceptsAll(Arrays.asList("a", "alias")).withRequiredArg();
		// optionParser.acceptsAll(Arrays.asList("h", "host", "hostmask")).withRequiredArg();
		// optionParser.acceptsAll(Arrays.asList("v", "verify")).requiresArgument();
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
		moduleInfo.setName("Admin");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v0.1");
		moduleInfo.addCommand("!admin");
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		// We don't care about MessageEvents. We only care about PrivateMessageEvents
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		String m = event.getMessage();
		if (isCommand(m, "!admin"))
		{
			List<String> params = Arrays.asList(m.trim().split("\\s"));
			HashMap<String, List<String>> commandList = adminInfo.getCommands();
			if (params.size() > 1 && (commandList.containsKey(params.get(1).toLowerCase()) && isHelp(m)))
			{
				for (String message : commandList.get(params.get(1)))
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
	
	public abstract void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event);
	
	public UserService getUserService()
	{
		return userService;
	}
}
