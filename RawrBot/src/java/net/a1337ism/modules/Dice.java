package net.a1337ism.modules;

import java.util.Random;
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
	private static Logger	logger			= LoggerFactory.getLogger(RawrBot.class);
	private int				maxDies			= 1000;
	private int				maxLimit		= 1000;
	private int				defaultDies		= 1;
	private int				defaultLimit	= 20;

	// URL Regex matching
	static String			regex			= "(\\d+)?d(\\d+)([\\+\\-](\\d+))?";
	static Pattern			pattern			= Pattern.compile(regex);

	private int roll()
	{
		return defaultDies;
	}

	public static int randomNumber(int dice)
	{
		Random random = new Random();
		int randomNumber = random.nextInt(dice);
		return randomNumber;
	}

	@Override
	public void onMessage(MessageEvent event)
	{
		if (event.getMessage().trim().toLowerCase().startsWith("!dice"))
		{
			if (event.getMessage().trim().toLowerCase().endsWith("-help")
					|| event.getMessage().trim().toLowerCase().endsWith("-h"))
			{
				String diceHelp = "!dice [dice notation] : Throws some dice and displays the result. Example: 1d20+10 or 3d6-2";
				ircUtil.sendMessage(event, diceHelp);
			}
			else
			{
				String message = event.getMessage().toLowerCase();
				if (pattern.matcher(message).find())
				{
					int numberDies = parseInt(pattern.matcher(message).group(1));
					int dieSize = parseInt(pattern.matcher(message).group(2));
					String mod = pattern.matcher(message).group(3);
					int modNumber = parseInt(pattern.matcher(message).group(4));

					if (numberDies != null && numberDies > 0)
					{

					}
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