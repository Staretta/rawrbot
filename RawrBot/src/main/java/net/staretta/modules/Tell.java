package net.staretta.modules;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

	public Tell()
	{
		service = RawrBot.applicationContext.getBean(TellService.class);
	}

	@Override
	protected ModuleInfo setModuleInfo()
	{
		moduleInfo = new ModuleInfo();
		moduleInfo.setName("Tell");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.0");
		moduleInfo.addCommand("!tell", "!tell <Nickname> <Message> : Tells the user the message, "
				+ "if they are offline, they will be notified of the message when they join channel.");
		moduleInfo
				.addCommand("!told",
						"!told [Nickname] : Displays messages to a user, or all users, and if a user has received the messages");
		// moduleInfo.addCommand("!note", "!note <Nickname> : Gives a note to a user");
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
		if (isCommand(event.getMessage(), "!tell"))
		{
			String[] params = event.getMessage().replace("!tell", "").trim().split("\\s");
			if (params.length > 1)
			{
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < params.length; i++)
					sb.append(params[i] + " ");
				String message = sb.toString().trim();
				String toNickname = params[0];

				if (service.addTell(event.getUser(), toNickname, message, event.getBot().getConfiguration()
						.getServerHostname()))
					event.getChannel().send().message(toNickname + " will be told: " + message);
			}
			else
			{
				event.getChannel().send().message(moduleInfo.getCommands().get("!tell"));
			}
		}
		else if (isCommand(event.getMessage(), "!told"))
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
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		if (isCommand(event.getMessage(), "!tell"))
		{
			String[] params = event.getMessage().replace("!tell", "").trim().split("\\s");
			if (params.length > 1)
			{
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < params.length; i++)
					sb.append(params[i] + " ");
				String message = sb.toString().trim();
				String toNickname = params[0];

				if (service.addTell(event.getUser(), toNickname, message, event.getBot().getConfiguration()
						.getServerHostname()))
					event.getUser().send().message(toNickname + " will be told: " + message);
			}
			else
			{
				event.getUser().send().message(moduleInfo.getCommands().get("!tell"));
			}
		}
		else if (isCommand(event.getMessage(), "!told"))
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
