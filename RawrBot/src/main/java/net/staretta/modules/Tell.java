package net.staretta.modules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.entity.TellEntity;
import net.staretta.businesslogic.services.TellService;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

// TODO: Add told date, and display the date they were told when using !told command.
public class Tell extends BaseListener
{
	private TellService service;
	
	private OptionParser parser;
	
	public Tell()
	{
		service = RawrBot.getAppCtx().getBean(TellService.class);
		
		parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("a", "all"));
		parser.acceptsAll(Arrays.asList("n", "nick", "nickname")).withRequiredArg();
		parser.acceptsAll(Arrays.asList("l", "limit")).withRequiredArg().ofType(Integer.class);
		parser.acceptsAll(Arrays.asList("d", "date", "dates")).withRequiredArg();
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Tell");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.1");
		moduleInfo.addCommand("!tell", "!tell <Nickname>|<Nickname,Nickname> <Message> : Tells the Nickname, or Nicknames,"
				+ " the message when they say something in channel. If they are offline,"
				+ " they will be told the message when they join channel.");
		moduleInfo
				.addCommand(
						"!told",
						
						"!told [Nickname] : Displays messages sent to a nickname, or all nicknames, and if a nickname has received the messages. "
								+ "The reply will always be sent as a Private Message.",
						"!told [-n|-nick <Nickname>] [-d|-date <mm-dd-yy|mm-dd-yy,mm-dd-yy>] [-l|-limit <Number>] : Displays messages based on optional filters. "
								+ "Nickname will filter results based on the person you sent a message to. Date will filter based on a single day, or a range of days. "
								+ "Limit, will display results up to the limit.",
						"Examples: \"!told -n Staretta -d 12-12-12 -l 20\" or \"!told -n Staretta -d 12-12-12,1-12-13 -l 50\"");
		moduleInfo.addCommand("!note",
				"!note <Nickname>|<Nickname,Nickname> <Message> : Tells the Nickname, or Nicknames, the message when they say something in channel. "
						+ "If they are offline, they will be told the message when they join channel.");
		return moduleInfo;
	}
	
	@Override
	public void onJoin(JoinEvent<PircBotX> event) throws Exception
	{
		if (!event.getUser().getNick().equals(event.getBot().getNick()))
		{
			ArrayList<TellEntity> tells = service
					.getTells(event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());
			if (tells != null)
			{
				for (TellEntity tell : tells)
				{
					String date = "[" + new SimpleDateFormat("MM/dd/yy HH:mm:ss z").format(tell.getDate()) + "]";
					String message = date + " <" + tell.getFromNickname() + "> " + tell.getMessage();
					
					if (!tell.getChannel().isEmpty())
						event.getBot().sendIRC().message(tell.getChannel(), event.getUser().getNick() + ": " + message);
					else
						event.getUser().send().message(message);
					
					tell.setTold(true);
					tell.setToldDate(new Date());
					service.setTold(tell);
				}
			}
		}
	}
	
	@Override
	public void onAction(ActionEvent<PircBotX> event) throws Exception
	{
		if (!event.getUser().getNick().equals(event.getBot().getNick()))
		{
			ArrayList<TellEntity> tells = service
					.getTells(event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());
			if (tells != null)
			{
				for (TellEntity tell : tells)
				{
					String date = "[" + new SimpleDateFormat("MM/dd/yy HH:mm:ss z").format(tell.getDate()) + "]";
					String message = date + " <" + tell.getFromNickname() + "> " + tell.getMessage();
					
					if (!tell.getChannel().isEmpty())
						event.getBot().sendIRC().message(tell.getChannel(), event.getUser().getNick() + ": " + message);
					else
						event.getUser().send().message(message);
					
					tell.setTold(true);
					tell.setToldDate(new Date());
					service.setTold(tell);
				}
			}
		}
	}
	
	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		if (!event.getUser().getNick().equals(event.getBot().getNick()))
		{
			// Check to see if the user has anything we need to tell them
			ArrayList<TellEntity> tells = service
					.getTells(event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());
			if (tells != null)
			{
				for (TellEntity tell : tells)
				{
					String date = "[" + new SimpleDateFormat("MM/dd/yy HH:mm:ss z").format(tell.getDate()) + "]";
					String message = date + " <" + tell.getFromNickname() + "> " + tell.getMessage();
					
					if (!tell.getChannel().isEmpty())
						event.getBot().sendIRC().message(tell.getChannel(), event.getUser().getNick() + ": " + message);
					else
						event.getUser().send().message(message);
					
					tell.setTold(true);
					tell.setToldDate(new Date());
					service.setTold(tell);
				}
			}
		}
		
		if (isCommand(event.getMessage(), "!tell") || isCommand(event.getMessage(), "!note"))
		{
			String[] params = null;
			if (isCommand(event.getMessage(), "!tell"))
				params = event.getMessage().replaceFirst("!tell", "").trim().split("\\s");
			else
				params = event.getMessage().replaceFirst("!note", "").trim().split("\\s");
			
			// If params contains a nickname and a message, the length should be greater than 1.
			if (params.length > 1)
			{
				// If the nickname has a comma, we know they want to send to multiple nicknames.
				// IRC doesn't allow comma's in the nickname, so this should work.
				String[] nicknames;
				if (params[0].contains(","))
				{
					nicknames = params[0].split(",");
				}
				else
				{
					nicknames = new String[] { params[0] };
				}
				
				String message = StringUtils.join(params, " ", 1, params.length);
				
				for (String nick : nicknames)
				{
					// APPARENTLY WE NEED TO CHECK IF THE NICKNAME IS THE BOT, AND DISCARD IT. BECAUSE USERS.
					if (!nick.toLowerCase().equals(event.getBot().getNick().toLowerCase()))
					{
						if (service.addTell(event.getUser(), nick, message, event.getBot().getConfiguration().getServerHostname(), event
								.getChannel().getName()))
						{
							event.getChannel().send().message(nick + " will be told: " + message);
						}
					}
				}
			}
			else
			{
				List<String> messages;
				if (isCommand(event.getMessage(), "!tell"))
					messages = getModuleInfo().getCommands().get("!tell");
				else
					messages = getModuleInfo().getCommands().get("!note");
				
				for (String message : messages)
				{
					event.getChannel().send().message(message);
				}
			}
		}
		
		else if (isCommand(event.getMessage(), "!told"))
		{
			
			String[] params = event.getMessage().replaceFirst("!told", "").trim().split("\\s");
			OptionSet options;
			ArrayList<TellEntity> tolds = null;
			
			try
			{
				options = parser.parse(params);
				tolds = getTolds(params, options, tolds, event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());
			}
			catch (NullPointerException | OptionException e)
			{
				// message user about invalid options
			}
			catch (ParseException e)
			{
				// message user with invalid date
			}
			
			// If there are tolds available in the database, then send them to the user that requested them.
			if (tolds != null)
			{
				if (!tolds.isEmpty())
				{
					for (TellEntity told : tolds)
					{
						SimpleDateFormat date = new SimpleDateFormat("MM/dd/yy HH:mm:ss z");
						StringBuilder sb = new StringBuilder();
						if (told.isTold())
							sb.append("Notified: [" + date.format(told.getToldDate()) + "]");
						else
							sb.append("Notified: False");
						sb.append(" | Message: [" + date.format(told.getDate()) + "] " + "<" + told.getToNickname() + "> "
								+ told.getMessage());
						event.getUser().send().message(sb.toString());
					}
				}
			}
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		if (!event.getUser().getNick().equals(event.getBot().getNick()))
		{
			// Check to see if the user has anything we need to tell them
			ArrayList<TellEntity> tells = service
					.getTells(event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());
			if (tells != null)
			{
				for (TellEntity tell : tells)
				{
					String date = "[" + new SimpleDateFormat("MM/dd/yy HH:mm:ss z").format(tell.getDate()) + "]";
					String message = date + "<" + tell.getFromNickname() + "> " + tell.getMessage();
					
					event.getUser().send().message(message);
					
					tell.setTold(true);
					tell.setToldDate(new Date());
					service.setTold(tell);
				}
			}
		}
		
		if (isCommand(event.getMessage(), "!tell") || isCommand(event.getMessage(), "!note"))
		{
			String[] params = null;
			if (isCommand(event.getMessage(), "!tell"))
				params = event.getMessage().replaceFirst("!tell", "").trim().split("\\s");
			else
				params = event.getMessage().replaceFirst("!note", "").trim().split("\\s");
			
			// If params contains a nickname and a message, the length should be greater than 1.
			if (params.length > 1)
			{
				// If the nickname has a comma, we know they want to send to multiple nicknames.
				// IRC doesn't allow comma's in the nickname, so this should work.
				String[] nicknames;
				if (params[0].contains(","))
				{
					nicknames = params[0].split(",");
				}
				else
				{
					nicknames = new String[] { params[0] };
				}
				
				String message = StringUtils.join(params, " ", 1, params.length);
				
				for (String nick : nicknames)
				{
					// APPARENTLY WE NEED TO CHECK IF THE NICKNAME IS THE BOT, AND DISCARD IT. BECAUSE USERS.
					if (!nick.toLowerCase().equals(event.getBot().getNick().toLowerCase()))
					{
						if (service.addTell(event.getUser(), nick, message, event.getBot().getConfiguration().getServerHostname(), ""))
						{
							event.getUser().send().message(nick + " will be told: " + message);
						}
					}
				}
			}
			else
			{
				List<String> messages;
				if (isCommand(event.getMessage(), "!tell"))
					messages = getModuleInfo().getCommands().get("!tell");
				else
					messages = getModuleInfo().getCommands().get("!note");
				
				for (String message : messages)
				{
					event.getUser().send().message(message);
				}
			}
		}
		
		else if (isCommand(event.getMessage(), "!told"))
		{
			String[] params = event.getMessage().replaceFirst("!told", "").trim().split("\\s");
			OptionSet options;
			ArrayList<TellEntity> tolds = null;
			
			try
			{
				options = parser.parse(params);
				tolds = getTolds(params, options, tolds, event.getUser().getNick(), event.getBot().getConfiguration().getServerHostname());
			}
			catch (NullPointerException | OptionException e)
			{
				// message user about invalid options
			}
			catch (ParseException e)
			{
				// message user with invalid date
			}
			
			// If there are tolds available in the database, then send them to the user that requested them.
			if (tolds != null)
			{
				if (!tolds.isEmpty())
				{
					for (TellEntity told : tolds)
					{
						SimpleDateFormat date = new SimpleDateFormat("MM/dd/yy HH:mm:ss z");
						StringBuilder sb = new StringBuilder();
						if (told.isTold())
							sb.append("Notified: [" + date.format(told.getToldDate()) + "]");
						else
							sb.append("Notified: False");
						sb.append(" | Message: [" + date.format(told.getDate()) + "] " + "<" + told.getToNickname() + "> "
								+ told.getMessage());
						event.getUser().send().message(sb.toString());
					}
				}
			}
		}
	}
	
	/**
	 * Gets who has been told what based on a specific set of filters.
	 * 
	 */
	private ArrayList<TellEntity> getTolds(String[] params, OptionSet options, ArrayList<TellEntity> tolds, String fromNickname,
			String server) throws ParseException
	{
		// If there are no options, then all they entered was !told, or if it's -all switch
		if (!options.hasOptions() || options.has("all"))
		{
			if (options.has("all") || params[0].isEmpty())
			{
				tolds = service.getTolds(fromNickname, null, server, 5, null);
			}
			else
			{
				String[] nicknames = params[0].toString().split(",");
				tolds = service.getTolds(fromNickname, nicknames, server, 5, null);
			}
		}
		else
		{
			String[] nicknames = null;
			if (options.hasArgument("nick"))
			{
				nicknames = options.valueOf("nick").toString().split(",");
			}
			
			int limit = 5;
			if (options.hasArgument("limit"))
			{
				limit = (int) options.valueOf("limit");
			}
			
			Date[] dates = null;
			if (options.hasArgument("date"))
			{
				String[] inputDate = options.valueOf("date").toString().split(",");
				SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy");
				if (inputDate.length == 2)
				{
					dates = new Date[2];
					dates[0] = format.parse(inputDate[0]);
					dates[1] = format.parse(inputDate[1]);
				}
				else
				{
					dates = new Date[2];
					dates[0] = format.parse(inputDate[0]);
					Calendar c = Calendar.getInstance();
					c.setTime(dates[0]);
					c.add(Calendar.DATE, 1);
					dates[1] = c.getTime();
				}
			}
			
			// Get tolds using parameters that have been set. We'll check the parameters in the tell service
			// we're calling to see if any of them are null and not include them.
			tolds = service.getTolds(fromNickname, nicknames, server, limit, dates);
		}
		return tolds;
	}
}
