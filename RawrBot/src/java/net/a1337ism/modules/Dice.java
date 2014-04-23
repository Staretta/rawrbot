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

				// If the message only consists of !dice or !roll, throw a standard 1d20
				if (userMessage.equalsIgnoreCase("!dice") || userMessage.equalsIgnoreCase("!roll"))
				{
					String defaultRoll = event.getUser().getNick() + " rolled " + numberDies + "d" + dieSize + " = "
							+ randomNumber(dieSize);
					ircUtil.sendMessage(event, defaultRoll);
				}
				// If we find a match to our regex, then roll whatever the regex finds!
				else if (pattern.matcher(userMessage).find())
				{

					Matcher match = pattern.matcher(userMessage);
					String message = event.getUser().getNick() + " rolled ";
					String rolled = "";
					int total = 0;

					while (match.find())
					{
						// group 1 is the modifier, +, -, *, /
						// group 3 is the number of dice
						// group 4 is the number of sides on the dice
						// group 5 is the modifier amount
						String mod = match.group(1);
						int modNumber = 0;

						// if we have a modifier, then lets stuff with it
						if (match.group(1) != null)
						{
							// if the modifier is +, then add + to the message, and see if it's a dice roll or a regular
							// number
							if (match.group(1).startsWith("+"))
							{
								rolled += "+";

								if (match.group(2).contains("d"))
								{
									if (parseInt(match.group(3)) > 0)
									{
										numberDies = parseInt(match.group(3));
										rolled += numberDies;
									}

									if (parseInt(match.group(4)) > 0)
									{
										dieSize = parseInt(match.group(4));
										rolled += "d" + dieSize;
									}

									int number = randomNumber(numberDies, dieSize);
									total += number;
									rolled += "(" + number + ")";
								}
								else if (parseInt(match.group(5)) > 0)
								{
									modNumber = parseInt(match.group(5));
									rolled += "(" + modNumber + ")";
									total += modNumber;
								}
							}
							// if the modifier is -, then add - to the rolled, and see if it's a dice roll or a regular
							// number
							else if (match.group(1).startsWith("-"))
							{
								rolled += "-";

								if (match.group(2).contains("d"))
								{
									if (parseInt(match.group(3)) > 0)
									{
										numberDies = parseInt(match.group(3));
										rolled += numberDies;
									}

									if (parseInt(match.group(4)) > 0)
									{
										dieSize = parseInt(match.group(4));
										rolled += "d" + dieSize;
									}

									int number = randomNumber(numberDies, dieSize);
									total -= number;
									rolled += "(" + number + ")";

								}
								else if (parseInt(match.group(5)) > 0)
								{
									modNumber = parseInt(match.group(5));
									total -= modNumber;
									rolled += "(" + modNumber + ")";
								}
							}
						}
						// At this point we know it's just the beginning of the roll, and is probably a regular dice
						else if (match.group(2).contains("d"))
						{
							if (parseInt(match.group(3)) > 0)
							{
								numberDies = parseInt(match.group(3));
								rolled += numberDies;
							}

							if (parseInt(match.group(4)) > 0)
							{
								dieSize = parseInt(match.group(4));
								rolled += "d" + dieSize;
							}

							int number = randomNumber(numberDies, dieSize);
							total += number;
							rolled += "(" + number + ")";
						}
					}

					if (rolled.length() == 0)
					{
						String defaultRoll = event.getUser().getNick() + " rolled " + numberDies + "d" + dieSize
								+ " = " + randomNumber(dieSize);
						ircUtil.sendMessage(event, defaultRoll);
					}
					else
					{
						ircUtil.sendMessage(event, message + rolled + " = " + total);
					}
				}
				// If the regex doesn't find anything, then just roll a standard 1d20
				else
				{
					String defaultRoll = event.getUser().getNick() + " rolled " + numberDies + "d" + dieSize + " = "
							+ randomNumber(dieSize);
					ircUtil.sendMessage(event, defaultRoll);
				}
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