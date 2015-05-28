package net.staretta.modules.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.staretta.RawrBot;
import net.staretta.businesslogic.admin.AdminInfo;
import net.staretta.businesslogic.admin.AdminListener;
import net.staretta.businesslogic.services.UserService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.common.collect.ImmutableSet;

public class Admin extends AdminListener
{
	private UserService userService;
	
	public Admin()
	{
		userService = RawrBot.getAppCtx().getBean(UserService.class);
	}
	
	@Override
	public AdminInfo setAdminInfo()
	{
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setAdminVersion("v0.1");
		adminInfo.addCommand("register", "");
		adminInfo.addCommand("identify");
		adminInfo.addCommand("email");
		adminInfo.addCommand("password");
		adminInfo.addCommand("verify");
		return adminInfo;
	}
	
	@Override
	public void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		String m = event.getMessage();
		List<String> params = Arrays.asList(m.split("\\s"));
		// Check and see if all they entered was !admin, and if so, spit out the admin commands.
		if (m.trim().equalsIgnoreCase("!admin"))
		{
			ImmutableSet<Listener<PircBotX>> listeners = event.getBot().getConfiguration().getListenerManager().getListeners();
			StringBuilder commands = new StringBuilder();
			for (Listener<PircBotX> mod : listeners)
			{
				if (AdminListener.class.isAssignableFrom(mod.getClass()))
				{
					AdminListener listener = (AdminListener) mod;
					HashMap<String, List<String>> commandList = listener.getAdminInfo().getCommands();
					for (Entry<String, List<String>> command : commandList.entrySet())
					{
						if (!command.getKey().isEmpty())
						{
							commands.append(command.getKey() + " ");
						}
					}
				}
			}
			List<String> commandHelp = Arrays.asList("Admin Commands: " + commands.toString(),
					"For command specific help, type \"--help\" or \"-h\" after a command.");
			commandHelp.forEach(message -> event.getUser().send().message(message));
		}
		else if (isOption(m, "r", "register"))
		{
			// Check the size, verify that the user entered a valid email address, and the password isn't unreasonably small or large.
			// Then create the user and save it to the database
			// !admin register email password
			if (params.size() == 4)
			{
				if (userService.isValidEmail(params.get(2)) && userService.isValidPassword(params.get(3)))
				{
					
				}
			}
		}
		else if (isOption(m, "i", "identify"))
		{
			// !admin identify password
			if (params.size() == 3)
			{
				
			}
		}
		else if (isOption(m, "p", "pass", "password"))
		{
			if (params.size() == 3)
			{
				
			}
		}
		else if (isOption(m, "e", "email"))
		{
			if (params.size() == 3)
			{
				
			}
		}
		else if (isOption(m, "v", "verify"))
		{
			if (params.size() == 3)
			{
				
			}
		}
	}
}
