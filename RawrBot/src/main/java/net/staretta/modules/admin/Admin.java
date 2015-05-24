package net.staretta.modules.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.staretta.businesslogic.admin.AdminInfo;
import net.staretta.businesslogic.admin.AdminListener;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.common.collect.ImmutableSet;

public class Admin extends AdminListener
{
	
	public Admin()
	{
		
	}
	
	@Override
	public AdminInfo setAdminInfo()
	{
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setAdminVersion("v0.1");
		adminInfo.addCommand("register");
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
		else if (isOption(m, new String[] { "r", "register" }))
		{
			
		}
		else if (isOption(m, new String[] { "i", "identify" }))
		{
			
		}
		else if (isOption(m, new String[] { "p", "pass", "password" }))
		{
			
		}
		else if (isOption(m, new String[] { "e", "email" }))
		{
			
		}
		else if (isOption(m, new String[] { "v", "verify" }))
		{
			
		}
	}
}
