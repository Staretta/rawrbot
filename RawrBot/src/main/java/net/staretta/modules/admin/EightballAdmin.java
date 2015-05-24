package net.staretta.modules.admin;

import net.staretta.businesslogic.admin.AdminInfo;
import net.staretta.businesslogic.admin.AdminListener;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class EightballAdmin extends AdminListener
{
	public EightballAdmin()
	{
	}
	
	@Override
	public AdminInfo setAdminInfo()
	{
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setAdminVersion("v0.1");
		adminInfo.addCommand("eightball");
		adminInfo.addCommand("8ball");
		return adminInfo;
	}
	
	@Override
	public void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		if (isOption(event.getMessage(), "eightball") || isOption(event.getMessage(), "8ball"))
		{
			
		}
	}
}
