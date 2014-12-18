package net.staretta.modules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.entity.TellEntity;
import net.staretta.businesslogic.services.TellService;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Add parameters to told, allowing for finer control over date ranges, filter true tolds, limits to the amount of
// history to display to the user. Use jopt-simple.
// TODO: Add told date, and display the date they were told when using !told command.
// TODO: Add syntax to !tell to allow for specifying multiple users in a tell message.
public class Tell extends BaseListener
{
	TellService service;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private OptionParser parser;

	public Tell()
	{
		service = RawrBot.applicationContext.getBean(TellService.class);

		parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("a", "all"));
		parser.acceptsAll(Arrays.asList("n", "nick", "nickname")).withRequiredArg();
		parser.acceptsAll(Arrays.asList("l", "limit")).withRequiredArg().ofType(Integer.class);
		parser.acceptsAll(Arrays.asList("d", "date")).withRequiredArg();
	}

	@Override
	protected ModuleInfo setModuleInfo()
	{
		moduleInfo = new ModuleInfo();
		moduleInfo.setName("Tell");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.0");
		moduleInfo
				.addCommand(
						"!tell",
						"!tell <Nickname>|<Nickname,Nickname> <Message> : Tells the Nickname, or Nicknames, the message when they say something in channel. "
								+ "If they are offline, they will be told the message when they join channel.");
		moduleInfo
				.addCommand(
						"!told",
						"!told [Nickname] : Displays messages sent to a nickname, or all nicknames, and if a nickname has received the messages.");
		moduleInfo
				.addCommand(
						"!note",
						"!note <Nickname>|<Nickname,Nickname> <Message> : Tells the Nickname, or Nicknames, the message when they say something in channel. "
								+ "If they are offline, they will be told the message when they join channel.");
		return moduleInfo;
	}

	@Override
	public void onJoin(JoinEvent<PircBotX> event) throws Exception
	{
		super.onJoin(event);

		if (!event.getUser().getNick().equals(event.getBot().getNick()))
		{
			ArrayList<TellEntity> tells = service.getTells(event.getUser().getNick(), event.getBot().getConfiguration()
					.getServerHostname());
			if (tells != null)
			{
				for (TellEntity tell : tells)
				{
					String date = "[" + new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(tell.getDate()) + "]";
					String message = date + " <" + tell.getFromNickname() + "> " + tell.getMessage();
					event.getUser().send().notice(message);

					tell.setTold(true);
					service.setTold(tell);
				}
			}
		}
	}

	@Override
	public void OnMessage(MessageEvent event)
	{
		if (!event.getUser().getNick().equals(event.getBot().getNick()))
		{
			ArrayList<TellEntity> tells = service.getTells(event.getUser().getNick(), event.getBot().getConfiguration()
					.getServerHostname());
			if (tells != null)
			{
				for (TellEntity tell : tells)
				{
					String date = "[" + new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(tell.getDate()) + "]";
					String message = date + " <" + tell.getFromNickname() + "> " + tell.getMessage();
					event.getUser().send().notice(message);

					tell.setTold(true);
					service.setTold(tell);
				}
			}
		}

		if (isCommand(event.getMessage(), "!tell"))
		{
			String[] params = event.getMessage().replace("!tell", "").trim().split("\\s");

			// If params contains a nickname and a message, the length should be greater than 1.
			if (params.length > 1)
			{
				// If the nickname has a comma, we know they want to send to multiple nicknames.
				// IRC doesn't allow comma's in the nickname, so this should work fine.
				String[] nicknames;
				if (params[0].contains(","))
				{
					nicknames = params[0].split(",");
				}
				else
				{
					nicknames = new String[1];
					nicknames[0] = params[0];
				}

				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < params.length; i++)
					sb.append(params[i] + " ");
				String message = sb.toString().trim();

				for (String nick : nicknames)
				{
					if (service.addTell(event.getUser(), nick, message, event.getBot().getConfiguration()
							.getServerHostname()))
					{
						event.getChannel().send().message(nick + " will be told: " + message);
					}
				}
			}
			else
			{
				event.getChannel().send().message(moduleInfo.getCommands().get("!tell"));
			}
		}
		else if (isCommand(event.getMessage(), "!told"))
		{
			// Check if the user is not identified with nickserv, and if the user's previous messages are from an
			// identified account. If it doesn't pass, then tell them they need to identify with nickserv first.
			if (!event.getUser().isVerified()
					&& service.isVerified(event.getUser().getNick(), event.getBot().getConfiguration()
							.getServerHostname()))
			{
				event.getUser()
						.send()
						.message(
								"You need to be identified with NickServ to access this "
										+ "!told information. \"/msg NickServ identify <password>\"");
			}
			else
			{
				String[] params = event.getMessage().replaceFirst("!told", "").trim().split("\\s");
				OptionSet options;
				ArrayList<TellEntity> tolds = null;

				try
				{
					options = parser.parse(params);
					tolds = getTolds(params, options, tolds, event.getUser().getNick(), event.getBot()
							.getConfiguration().getServerHostname());
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
					for (TellEntity told : tolds)
					{
						String message = "Told:" + told.isTold() + " ["
								+ new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(told.getDate()) + "] <"
								+ told.getToNickname() + "> " + told.getMessage();
						event.getUser().send().message(message);
					}
				}
			}
		}
	}

	private ArrayList<TellEntity> getTolds(String[] params, OptionSet options, ArrayList<TellEntity> tolds,
			String fromNickname, String server) throws ParseException
	{
		// If there are no options, then all they entered was !told, or if it's -all switch
		if (!options.hasOptions() || options.has("all"))
		{
			String toNickname = params[0];
			tolds = service.getTolds(fromNickname, toNickname, server);

			tolds = service.getAllTolds(fromNickname, server);
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
					dates = new Date[1];
					dates[0] = format.parse(inputDate[0]);
				}
			}

			// Get tolds using parameters that have been set. We'll check the parameters in the tell service
			// we're calling to see if any of them are null and not include them.
			tolds = null;
		}
		return tolds;
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		ArrayList<TellEntity> tells = service.getTells(event.getUser().getNick(), event.getBot().getConfiguration()
				.getServerHostname());
		if (tells != null)
		{
			for (TellEntity tell : tells)
			{
				String date = "[" + new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(tell.getDate()) + "]";
				String message = date + " <" + tell.getFromNickname() + "> " + tell.getMessage();
				event.getUser().send().message(message);

				tell.setTold(true);
				service.setTold(tell);
			}
		}

		if (isCommand(event.getMessage(), "!tell"))
		{
			String[] params = event.getMessage().replace("!tell", "").trim().split("\\s");

			// If params contains a nickname and a message, the length should be greater than 1.
			if (params.length > 1)
			{
				// If the nickname has a comma, we know they want to send to multiple nicknames.
				// IRC doesn't allow comma's in the nickname, so, this should work fine.
				String[] nicknames;
				if (params[0].contains(","))
				{
					nicknames = params[0].split(",");
				}
				else
				{
					nicknames = new String[1];
					nicknames[0] = params[0];
				}

				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < params.length; i++)
					sb.append(params[i] + " ");
				String message = sb.toString().trim();

				for (String nick : nicknames)
				{
					if (service.addTell(event.getUser(), nick, message, event.getBot().getConfiguration()
							.getServerHostname()))
					{
						event.getUser().send().message(nick + " will be told: " + message);
					}
				}
			}
			else
			{
				event.getUser().send().message(moduleInfo.getCommands().get("!tell"));
			}
		}
		else if (isCommand(event.getMessage(), "!told"))
		{

			if (!event.getUser().isVerified()
					&& service.isVerified(event.getUser().getNick(), event.getBot().getConfiguration()
							.getServerHostname()))
			{
				event.getUser()
						.send()
						.message(
								"You need to be identified to access this !told information. \"/msg nickserv identify <password>\"");
			}
			else
			{
				String[] params = event.getMessage().replaceFirst("!told", "").trim().split("\\s");
				ArrayList<TellEntity> tolds;
				if (params[0].equals("-a") || params[0].equals("-all") || params[0].equals(""))
				{
					tolds = service.getAllTolds(event.getUser().getNick(), event.getBot().getConfiguration()
							.getServerHostname());
				}
				else
				{
					String toNickname = params[0];
					tolds = service.getTolds(event.getUser().getNick(), toNickname, event.getBot().getConfiguration()
							.getServerHostname());
				}

				if (tolds != null)
				{
					for (TellEntity told : tolds)
					{
						String message = "Told:" + told.isTold() + " ["
								+ new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(told.getDate()) + "] <"
								+ told.getToNickname() + "> " + told.getMessage();
						event.getUser().send().message(message);
					}
				}
			}

			// OptionParser parser = new OptionParser();
			// parser.acceptsAll(Arrays.asList("a", "all"));
			// parser.acceptsAll(Arrays.asList("n", "nick", "nickname")).withRequiredArg();
			// parser.acceptsAll(Arrays.asList("limit", "l")).withRequiredArg().ofType(Integer.class).d;
			// parser.acceptsAll(Arrays.asList("d", "date")).withRequiredArg();
			//
			// try
			// {
			// OptionSet options = parser.parse(params);
			// if (options.hasArgument("nick") || options.hasArgument("n"))
			// {
			// StringBuilder sb = new StringBuilder();
			// List<?> list = options.nonOptionArguments();
			// for (int i = 0; i < list.size(); i++)
			// sb.append(list.get(i).toString() + " ");
			// event.getUser().send().message(sb.toString().trim());
			// }
			// }
			// catch (Exception e)
			// {
			//
			// }
		}
	}
}
