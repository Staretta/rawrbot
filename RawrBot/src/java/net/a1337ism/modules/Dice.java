package net.a1337ism.modules;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.a1337ism.RawrBot;
import net.a1337ism.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dice extends ListenerAdapter
{
	// Logger shit
	private static Logger	logger				= LoggerFactory.getLogger(RawrBot.class);
	private int				maxDies				= 1000;
	private int				maxLimit			= 1000;
	private int				defaultNumberDies	= 1;
	private int				defaultDieSize		= 20;

	// URL Regex matching
	static String			regex				= "([\\+\\-])?\\s*((\\d*)d(\\d+)|(\\d+))";
	// static String regex = "([\\+\\-])?((\\d*)d(\\d+)|(\\d+))";
	// static String regex = "(([\\+\\-])?(\\d*)d(\\d+))|(([\\+\\-])(\\d+))";
	static Pattern			pattern				= Pattern.compile(regex);

	public int randomNumber(int dieSize)
	{
		Random random = new Random();
		int n = random.nextInt(dieSize) + 1;
		return n;
	}

	private int randomNumber(int numberDies, int dieSize)
	{
		Random random = new Random();
		int n = 0;

		for (int i = 1; i <= numberDies; i++)
		{
			n += randomNumber(dieSize);
		}
		return n;
	}

	@Override
	public void onMessage(MessageEvent event)
	{
		String userMessage = event.getMessage().trim().toLowerCase();
		if ((userMessage.startsWith("!dice") || userMessage.startsWith("!roll"))
				&& !RateLimiter.isRateLimited(event.getUser().getNick(), 20))
		{
			if (userMessage.endsWith("-help") || userMessage.endsWith("-h"))
			{
				String diceHelp = "!dice/!roll [dice notation] : Throws some dice and displays the result. Example: 1d20+10 or 3d6-2";
				ircUtil.sendMessage(event, diceHelp);
			}
			else
			{
				int numberDies = defaultNumberDies;
				int dieSize = defaultDieSize;

				if (userMessage.equalsIgnoreCase("!dice") || userMessage.equalsIgnoreCase("!roll"))
				{
					String message = event.getUser().getNick() + " rolled " + numberDies + "d" + dieSize + " = "
							+ randomNumber(dieSize);
					ircUtil.sendMessage(event, message);
				}

				Matcher match = pattern.matcher(userMessage);

				String message = event.getUser().getNick() + " rolled ";
				int total = 0;

				while (match.find())
				{
					String mod = match.group(1);
					int modNumber = 0;

					if (match.group(1) != null)
					{
						if (match.group(1).startsWith("+"))
						{
							message += "+";

							if (match.group(2).contains("d"))
							{
								if (parseInt(match.group(3)) > 0)
								{
									numberDies = parseInt(match.group(3));
									message += numberDies;
								}

								if (parseInt(match.group(4)) > 0)
								{
									dieSize = parseInt(match.group(4));
									message += "d" + dieSize;
								}

								int number = randomNumber(numberDies, dieSize);
								total += number;
								message += "(" + number + ")";
							}
							else if (parseInt(match.group(5)) > 0)
							{
								modNumber = parseInt(match.group(5));
								message += "(" + modNumber + ")";
								total += modNumber;
							}
						}
						else if (match.group(1).startsWith("-"))
						{
							message += "-";

							if (match.group(2).contains("d"))
							{
								if (parseInt(match.group(3)) > 0)
								{
									numberDies = parseInt(match.group(3));
									message += numberDies;
								}

								if (parseInt(match.group(4)) > 0)
								{
									dieSize = parseInt(match.group(4));
									message += "d" + dieSize;
								}

								int number = randomNumber(numberDies, dieSize);
								total -= number;
								message += "(" + number + ")";

							}
							else if (parseInt(match.group(5)) > 0)
							{
								modNumber = parseInt(match.group(5));
								total -= modNumber;
								message += "(" + modNumber + ")";
							}
						}
					}
					else if (match.group(2).contains("d"))
					{
						if (parseInt(match.group(3)) > 0)
						{
							numberDies = parseInt(match.group(3));
							message += numberDies;
						}

						if (parseInt(match.group(4)) > 0)
						{
							dieSize = parseInt(match.group(4));
							message += "d" + dieSize;
						}

						int number = randomNumber(numberDies, dieSize);
						total += number;
						message += "(" + number + ")";
					}
				}

				ircUtil.sendMessage(event, message + " = " + total);
			}
		}
	}

	public static int parseInt(String integer)
	{
		try
		{
			return Integer.parseInt(integer);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
}