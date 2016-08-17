package net.staretta.modules;

import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.Command;
import net.staretta.businesslogic.ModuleInfo;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.util.regex.Pattern;

public class Karma extends BaseListener
{
	String regex = "";
	Pattern pattern = Pattern.compile(regex);
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setName("karma");
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setVersion("v0.1");
		moduleInfo.addCommand(new Command("!karma", "!karma [name] - Show the user's karma count.",
				"!karma [-t|-top] - Show the top 5 karma earners.",
				"!karma [-b|-bottom] - Show the bottom 5 karma earners.",
				"!karma [-g|-giver] - Show who's given the most.",
				"!karma [-taker] - Show who's taken the most.",
				"<name>++ | <name>-- - Give or take karma from a user."));
		return moduleInfo;
	}

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception
	{
		super.onMessage(event);

		// Check not bot?
		// Check regex to see if someone is modding karma.
		// Check if user is upping their own karma and tell them no
	}

	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		// check is user is checking themselves, and if they have no karma, display, Don't be so hard on yourself.
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		// Do nothing
	}
}
