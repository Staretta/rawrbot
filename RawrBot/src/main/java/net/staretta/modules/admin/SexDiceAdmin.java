package net.staretta.modules.admin;

import net.staretta.RawrBot;
import net.staretta.businesslogic.admin.AdminInfo;
import net.staretta.businesslogic.admin.AdminListener;
import net.staretta.businesslogic.entity.UserEntity.Role;
import net.staretta.businesslogic.services.SexDiceService;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class SexDiceAdmin extends AdminListener
{
	private SexDiceService service;
	
	public SexDiceAdmin()
	{
		service = RawrBot.getAppCtx().getBean(SexDiceService.class);
	}
	
	@Override
	public AdminInfo setAdminInfo()
	{
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setAdminVersion("v0.1");
		adminInfo.addOption("eightball");
		return adminInfo;
	}
	
	@Override
	public void OnAdminPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		// If it's our command, do stuff! And if the user is logged in, and they are a superadmin, then do the good stuff.
		String m = event.getMessage();
		if (isOption(m, "sexdice"))
		{
			if (getUserService().isLoggedIn(event.getUser()) && getUserService().getUser(event.getUser()).getRole() == Role.SuperAdmin)
			{
				String[] params = event.getMessage().trim().split("\\s");
				if (isOption(m, 2, "-addAction", "-aA"))
				{
					service.addAction(StringUtils.join(params, " ", 3, params.length));
					event.getUser().send().message("Successfully added sexdice.");
				}
				else if (isOption(m, 2, "-addLocation", "-aL"))
				{
					service.addBodypart(StringUtils.join(params, " ", 3, params.length));
					event.getUser().send().message("Successfully added sexdice.");
				}
				else if (isOption(m, 2, "-addBodypart", "-aB"))
				{
					service.addLocation(StringUtils.join(params, " ", 3, params.length));
					event.getUser().send().message("Successfully added sexdice.");
				}
			}
		}
	}
}
