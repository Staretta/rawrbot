package net.staretta.modules.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.staretta.RawrBot;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.admin.AdminInfo;
import net.staretta.businesslogic.admin.AdminListener;
import net.staretta.businesslogic.entity.UserEntity.Role;
import net.staretta.businesslogic.services.EmailService;
import net.staretta.businesslogic.services.UserService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.common.collect.ImmutableSet;

public class Admin extends AdminListener
{
	private UserService userService;
	private EmailService emailService;
	
	public Admin()
	{
		userService = RawrBot.getAppCtx().getBean(UserService.class);
		emailService = RawrBot.getAppCtx().getBean(EmailService.class);
	}
	
	@Override
	public AdminInfo setAdminInfo()
	{
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setAdminVersion("v0.1");
		adminInfo.addOption("register");
		adminInfo.addOption("identify");
		adminInfo.addOption("email");
		adminInfo.addOption("password");
		adminInfo.addOption("verify");
		return adminInfo;
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.addCommand("!admin");
		return moduleInfo;
	}
	
	@Override
	public void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		String m = event.getMessage();
		// List<String> params = Arrays.asList(m.split("\\s"));
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
			register(event);
		}
		else if (isOption(m, "i", "identify"))
		{
			identify(event);
		}
		else if (isOption(m, "p", "pass", "password"))
		{
			password(event);
		}
		else if (isOption(m, "e", "email"))
		{
			email(event);
		}
		else if (isOption(m, "c", "channel"))
		{
			channel(event);
		}
		else if (isOption(m, "v", "verify"))
		{
			verify(event);
		}
	}
	
	private void register(PrivateMessageEvent<PircBotX> event)
	{
		List<String> params = Arrays.asList(event.getMessage().split("\\s"));
		// Check the size, verify that the user entered a valid email address, and the password isn't unreasonably small or large.
		// Then create the user and save it to the database
		// !admin register <email> <password>
		if (params.size() == 4)
		{
			String email = params.get(2);
			String password = params.get(3);
			if (!userService.isNicknameAvailable(event.getUser()))
			{
				event.getUser().send().message("Nickname already in use.");
			}
			else if (!userService.isValidEmail(email))
			{
				event.getUser().send().message("Invalid Email address, please enter a correct email address.");
			}
			else if (!userService.isValidPassword(password))
			{
				String message = "Invalid password. Passwords must be 8-31 characters long, " + "and contain at least 3 of the following:"
						+ " a Digit, an Uppercase Character, a Lowercase Character, or a Special Character.";
				event.getUser().send().message(message);
			}
			else
			{
				userService.createUser(emailService, event.getUser(), email, password);
				String message = "User Created - A verification email has been sent to the email provided. "
						+ "To verify the email: \"!admin verify <code from email>\"";
				event.getUser().send().message(message);
			}
		}
		else
		{
			event.getUser().send().message("Incorrect number of parameters. \"!admin register user@email.com P@sSw0rd\"");
		}
	}
	
	private void identify(PrivateMessageEvent<PircBotX> event)
	{
		List<String> params = Arrays.asList(event.getMessage().split("\\s"));
		// !admin identify password
		// TODO: !admin identify <password>
		// TODO: Check location of email in parameters, and use that to log them in? Except emails aren't unique. >.< Craaaaap.
		if (params.size() == 3)
		{
			String password = params.get(2);
			if (userService.isNicknameAvailable(event.getUser()))
			{
				event.getUser().send().message("Nickname not registered. \"!admin register user@email.com P@sSw0rd\"");
			}
			else if (!userService.isValidPassword(password))
			{
				String message = "Invalid password. Passwords must be 8-31 characters long, " + "and contain at least 3 of the following:"
						+ " a Digit, an Uppercase Character, a Lowercase Character, or a Special Character.";
				event.getUser().send().message(message);
			}
			else if (userService.isLoggedIn(event.getUser()))
			{
				event.getUser().send().message("You are already logged in.");
			}
			else
			{
				if (userService.checkPassword(event.getUser(), password))
				{
					userService.login(event.getUser());
					event.getUser().send().message("You are now logged in.");
				}
				else
				{
					event.getUser().send().message("Incorrect Password.");
				}
			}
		}
		else
		{
			event.getUser().send().message("Incorrect number of parameters. \"!admin identify <password>\"");
		}
	}
	
	private void password(PrivateMessageEvent<PircBotX> event)
	{
		List<String> params = Arrays.asList(event.getMessage().split("\\s"));
		if (params.size() == 3)
		{
			// TODO
		}
	}
	
	private void email(PrivateMessageEvent<PircBotX> event)
	{
		List<String> params = Arrays.asList(event.getMessage().split("\\s"));
		if (params.size() == 3)
		{
			// TODO
		}
	}
	
	private void channel(PrivateMessageEvent<PircBotX> event)
	{
		String m = event.getMessage();
		List<String> params = Arrays.asList(m.split("\\s"));
		// !admin channel add <#channel> (password)
		// !admin channel del <#channel>
		// !admin channel join <#channel> (password)
		// !admin channel quit <#channel>
		// !admin channel module add <module> <channel>
		// !admin channel module del <module> <channel>
		// TODO: Add and remove channels that a user is an operator of. By adding a channel, the user can control what the bot is able to do
		// in the channel?
		// TODO: Eventually change this so that users can add and remove the bot from the channel.
		// At the moment this will only respond to users who are superadmins.
		if (params.size() == 4 && userService.getUser(event.getUser()).getRole() == Role.SuperAdmin)
		{
			if (isOption(m, 2, "a", "add"))
			{
				// !admin channel add <#channel>
				if (params.size() == 4)
				{
					
				}
				// !admin channel add <#channel> (password)
				else if (params.size() == 5)
				{
					
				}
				else
				{
					event.getUser().send().message("Incorrect number of parameters. \"!admin channel add <#channel> (password)\"");
				}
			}
			else if (isOption(m, 2, "d", "del", "delete", "r", "remove"))
			{
				// !admin channel del <#channel>
				if (params.size() == 4)
				{
					
				}
				else
				{
					event.getUser().send().message("Incorrect number of parameters. \"!admin channel delete <#channel>\"");
				}
			}
			else if (isOption(m, 2, "j", "join"))
			{
				// !admin channel join <#channel>
				if (params.size() == 4)
				{
					
				}
				// !admin channel join <#channel> (password)
				else if (params.size() == 5)
				{
					
				}
				else
				{
					event.getUser().send().message("Incorrect number of parameters. \"!admin channel join <#channel> (password)\"");
				}
			}
			else if (isOption(m, 2, "q", "quit", "l", "leave"))
			{
				// !admin channel quit <#channel>
				if (params.size() == 4)
				{
					
				}
				else
				{
					event.getUser().send().message("Incorrect number of parameters. \"!admin channel quit <#channel>\"");
				}
			}
		}
	}
	
	private void verify(PrivateMessageEvent<PircBotX> event)
	{
		List<String> params = Arrays.asList(event.getMessage().split("\\s"));
		// !admin verify <verifcation code>
		// Verify their email is correct, because they should have received the email when they signed up.
		if (params.size() == 3)
		{
			if (userService.verifyEmail(event.getUser(), params.get(2)))
			{
				event.getUser().send().message("Email successfully verified. You can now login using \"!admin identify <password>\"");
			}
		}
		else
		{
			event.getUser().send().message("Incorrect number of parameters. \"!admin verify <code>\"");
		}
	}
}
