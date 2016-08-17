package net.staretta.modules;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.Command;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class Dice extends BaseListener
{
	private int maxNumberDies = 20;
	private int maxDieSize = 1000;
	private int defaultNumberDies = 1;
	private int defaultDieSize = 20;
	private int maxModNumber = 1000;
	
	// URL Regex matching
	static String regex = "([\\+\\-])?\\s*((\\d*)[dD](\\d+)|(\\d+))";
	// static String regex = "([\\+\\-])?((\\d*)d(\\d+)|(\\d+))";
	// static String regex = "(([\\+\\-])?(\\d*)d(\\d+))|(([\\+\\-])(\\d+))";
	static Pattern pattern = Pattern.compile(regex);
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Dice");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.0");
		moduleInfo.addCommand(new Command("!dice", "!dice [dice notation] : Throws some dice and displays the result. "
				+ "Example: 1d20+10 or 3d6-2"));
		moduleInfo.addCommand(new Command("!roll", "!roll [dice notation] : Throws some dice and displays the result. "
				+ "Example: 1d20+10 or 3d6-2"));
		return moduleInfo;
	}
	
	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		String userMessage = event.getMessage().trim().toLowerCase();
		if ((isCommand(event.getMessage(), "!dice") || isCommand(event.getMessage(), "!roll"))
				&& !RateLimiter.isRateLimited(event.getUser(), 20))
		{
			int numberDies = defaultNumberDies;
			int dieSize = defaultDieSize;
			
			// If the message only consists of !dice or !roll, throw a standard 1d20
			if (userMessage.equalsIgnoreCase("!dice") || userMessage.equalsIgnoreCase("!roll"))
			{
				String defaultRoll = event.getUser().getNick() + " rolled " + numberDies + "d" + dieSize + " = " + randomNumber(dieSize);
				event.getChannel().send().message(defaultRoll);
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
									if (numberDies > maxNumberDies)
										numberDies = maxNumberDies;
									rolled += numberDies;
								}
								
								if (parseInt(match.group(4)) > 0)
								{
									dieSize = parseInt(match.group(4));
									if (dieSize > maxDieSize)
										dieSize = maxDieSize;
									rolled += "d" + dieSize;
								}
								
								int number = randomNumber(numberDies, dieSize);
								total += number;
								rolled += "(" + number + ")";
							}
							else if (parseInt(match.group(5)) > 0)
							{
								modNumber = parseInt(match.group(5));
								if (modNumber > maxModNumber)
									modNumber = maxModNumber;
								total += modNumber;
								rolled += "(" + modNumber + ")";
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
									if (numberDies > maxNumberDies)
										numberDies = maxNumberDies;
									rolled += numberDies;
								}
								
								if (parseInt(match.group(4)) > 0)
								{
									dieSize = parseInt(match.group(4));
									if (dieSize > maxDieSize)
										dieSize = maxDieSize;
									rolled += "d" + dieSize;
								}
								
								int number = randomNumber(numberDies, dieSize);
								total -= number;
								rolled += "(" + number + ")";
								
							}
							else if (parseInt(match.group(5)) > 0)
							{
								modNumber = parseInt(match.group(5));
								if (modNumber > maxModNumber)
									modNumber = maxModNumber;
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
							if (numberDies > maxNumberDies)
								numberDies = maxNumberDies;
							rolled += numberDies;
						}
						
						if (parseInt(match.group(4)) > 0)
						{
							dieSize = parseInt(match.group(4));
							if (dieSize > maxDieSize)
								dieSize = maxDieSize;
							rolled += "d" + dieSize;
						}
						
						int number = randomNumber(numberDies, dieSize);
						total += number;
						rolled += "(" + number + ")";
					}
				}
				
				if (rolled.length() == 0)
				{
					String defaultRoll = event.getUser().getNick() + " rolled " + numberDies + "d" + dieSize + " = "
							+ randomNumber(dieSize);
					event.getChannel().send().message(defaultRoll);
				}
				else
				{
					event.getChannel().send().message(message + rolled + " = " + total);
				}
			}
			// If the regex doesn't find anything, then just roll a standard 1d20
			else
			{
				String defaultRoll = event.getUser().getNick() + " rolled " + numberDies + "d" + dieSize + " = " + randomNumber(dieSize);
				event.getChannel().send().message(defaultRoll);
			}
		}
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
	}
	
	public int randomNumber(int dieSize)
	{
		Random random = new Random();
		int n = random.nextInt(dieSize) + 1;
		return n;
	}
	
	private int randomNumber(int numberDies, int dieSize)
	{
		int n = 0;
		
		for (int i = 1; i <= numberDies; i++)
		{
			n += randomNumber(dieSize);
		}
		return n;
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