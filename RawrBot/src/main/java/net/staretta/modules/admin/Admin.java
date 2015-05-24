package net.staretta.modules.admin;

import net.staretta.businesslogic.admin.AdminListener;
import net.staretta.businesslogic.admin.AdminInfo;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class Admin extends AdminListener
{
	
	public Admin()
	{
	}
	
	@Override
	public AdminInfo setAdminInfo()
	{
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setAdminVersion("v0.1");
		return adminInfo;
	}
	
	@Override
	public void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		
	}
}
