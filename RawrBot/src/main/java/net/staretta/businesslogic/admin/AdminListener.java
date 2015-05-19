package net.staretta.businesslogic.admin;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public abstract class AdminListener extends BaseListener
{
	private AdminInfo adminInfo;
	
	public AdminListener()
	{
		adminInfo = setAdminInfo();
		
		// optionParser.acceptsAll(Arrays.asList("i", "identify"));
		// optionParser.acceptsAll(Arrays.asList("r", "register"));
		// optionParser.acceptsAll(Arrays.asList("p", "pass", "password")).requiredIf("identify", "register").requiresArgument();
		// optionParser.acceptsAll(Arrays.asList("e", "email")).requiredIf("register").requiresArgument();
		// optionParser.acceptsAll(Arrays.asList("a", "alias")).withRequiredArg();
		// optionParser.acceptsAll(Arrays.asList("h", "host", "hostmask")).withRequiredArg();
		// optionParser.acceptsAll(Arrays.asList("v", "verify")).requiresArgument();
	}
	
	public abstract AdminInfo setAdminInfo();
	
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
		String m = event.getMessage();
		if (isCommand(m, "!admin"))
		{
			if (m.toLowerCase().endsWith("-h") || m.toLowerCase().endsWith("-help") || m.toLowerCase().endsWith("--help"))
			{
				
				return;
			}
			OnAdminPrivateMessage(event);
		}
	}
	
	public abstract void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event);
}
