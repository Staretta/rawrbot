package net.staretta.modules;

import java.util.Random;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import net.staretta.businesslogic.services.RawrService;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rawr extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private RawrService service;

	public Rawr()
	{
		service = RawrBot.applicationContext.getBean(RawrService.class);
	}

	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Rawr");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v1.4");
		moduleInfo.addCommand("!rawr", "!rawr: Rawr. :3");
		moduleInfo.addCommand("!meow", "!meow : Meow! :3");
		moduleInfo.addCommand("!nyan", "!nyan : Nyan! :3");
		moduleInfo.addCommand("!woosh", "!woosh : Woosh! :3");
		return moduleInfo;
	}

	@Override
	public void OnMessage(MessageEvent event)
	{
		if (isCommand(event.getMessage(), "!rawr") && !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			if (event.getMessage().trim().startsWith("!RAWR"))
			{
				event.getChannel().send().message(service.getRandomRawr().toUpperCase());
			}
			else
			{
				event.getChannel().send().message(service.getRandomRawr());
			}
		} // Check if message starts with !meow, and if they are rate limited
		else if (isCommand(event.getMessage(), "!meow") && !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			if (event.getMessage().trim().startsWith("!MEOW"))
			{
				String meow = meowReplace(service.getRandomRawr());
				event.getChannel().send().message(meow.toUpperCase());
			}
			else
			{
				// If command starts with !meow.
				String meow = meowReplace(service.getRandomRawr());
				event.getChannel().send().message(meow);
			}
		} // Check if message starts with !nyan, and if they are rate limited
		else if (isCommand(event.getMessage(), "!nyan") && !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			// We want nyan to be weighted more than any other command. So we add more requests to their queue.
			RateLimiter.addRequest(event.getUser().getNick(), 4);
			// @formatter:off
			String[] nyan = { 
					"...,__,......,__,.....____________", 
					"`�.,�,.�*�`�.,�,.�*�..|::::::/\\:_|/\\",
					"`�.,�,.�*�`�.,�,.�*�.<|:::::(  o wo )", 
					"-.......-\"\"-.......--\"\"u\"''''u''''u\"" };
				// @formatter:on
			for (String line : nyan)
				event.getChannel().send().message(line);
		}
		else if (isCommand(event.getMessage(), "!woosh") && !RateLimiter.isRateLimited(event.getUser().getNick()))
		{
			Random random = new Random();
			String msg = "woo";
			for (int i = 1; i < random.nextInt(20); i++)
				msg += "o";
			msg += "shes!";
			event.getChannel().send().message(msg);
		}
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{

	}

	/**
	 * Replaces Rawr with Meow.<br>
	 * <br>
	 * We want to split Rawr into individual words in a list, and then for each word, replace the first instance of "R"
	 * with "M", and then replace "a" with "e", "w" with "o", and finally "r" with "w". We then merge the words back
	 * together into a message, and return message.
	 * 
	 * @param meow
	 *            String of Rawr's we want to replace
	 * @return a string of Meow's that we replaced.
	 */
	private String meowReplace(String meow)
	{
		String[] words = null;
		String message = "";
		words = meow.split("\\s");
		for (String word : words)
		{
			if (word.startsWith("R"))
			{
				word = word.replaceFirst("R", "M");
			}
			else
			{
				word = word.replaceFirst("r", "m");
			}
			word = word.replace("A", "E").replace("a", "e");
			word = word.replace("W", "O").replace("w", "o");
			word = word.replace("R", "W").replace("r", "w");
			message += word + " ";
		}

		return message;
	}
}