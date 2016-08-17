package net.staretta.modules.admin;

import net.staretta.RawrBot;
import net.staretta.businesslogic.Option;
import net.staretta.businesslogic.admin.AdminInfo;
import net.staretta.businesslogic.admin.AdminListener;
import net.staretta.businesslogic.entity.UserEntity.Role;
import net.staretta.businesslogic.services.EightballService;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class EightballAdmin extends AdminListener
{
	private EightballService service;
	
	public EightballAdmin()
	{
		service = RawrBot.getAppCtx().getBean(EightballService.class);
	}
	
	@Override
	public AdminInfo setAdminInfo()
	{
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setAdminVersion("v0.1");
		adminInfo.addOption(new Option(""));
		return adminInfo;
	}
	
	@Override
	public void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		// If it's our command, do stuff! And if the user is logged in, and they are a superadmin, then do the good stuff.
		String m = event.getMessage();
		if (isOption(m, "eightball", "8ball", "8-ball"))
		{
			if (getUserService().isLoggedIn(event.getUser()) && getUserService().getUser(event.getUser()).getRole() == Role.SuperAdmin)
			{
				if (isOption(m, 2, "-add", "-a"))
				{
					String[] params = event.getMessage().trim().split("\\s");
					getLogger().info(StringUtils.join(params, " ", 3, params.length));
					service.addAnswer(StringUtils.join(params, " ", 3, params.length));
					event.getUser().send().message("Successfully added new 8ball answer.");
				}
			}
		}
	}
	
}
