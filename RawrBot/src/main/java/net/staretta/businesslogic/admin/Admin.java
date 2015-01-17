package net.staretta.businesslogic.admin;

import java.util.Arrays;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Admin extends BaseListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	private OptionParser parser;

	public Admin()
	{
		parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("i", "identify"));
		parser.acceptsAll(Arrays.asList("r", "register"));
		parser.acceptsAll(Arrays.asList("p", "pass", "password")).requiredIf("identify", "register").requiresArgument();
		parser.acceptsAll(Arrays.asList("e", "email")).requiredIf("register").requiresArgument();
		parser.acceptsAll(Arrays.asList("a", "alias")).withRequiredArg();
		parser.acceptsAll(Arrays.asList("h", "host", "hostmask")).withRequiredArg();
		parser.acceptsAll(Arrays.asList("v", "verify")).requiresArgument();
	}

	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("Admin");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v0.1");
		moduleInfo.addCommand("!admin");
		return moduleInfo;
	}

	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		// We don't care about MessageEvents. We only care about PrivateMessageEvents
	}

	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		if (isCommand(event.getMessage(), "!admin"))
		{
			String[] params = event.getMessage().replaceFirst("!admin", "").trim().split("\\s");
			OptionSet options = null;

			try
			{
				options = parser.parse(params);
			}
			catch (NullPointerException | OptionException e)
			{
				// message user about invalid options
			}

			if (options != null)
			{

			}
		}
	}
}
