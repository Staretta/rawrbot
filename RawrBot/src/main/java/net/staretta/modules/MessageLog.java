package net.staretta.modules;

import java.util.Date;

import net.staretta.RawrBot;
import net.staretta.businesslogic.entity.MessageLogEntity.MessageType;
import net.staretta.businesslogic.entity.MessageLogEntity.Role;
import net.staretta.businesslogic.services.MessageLogService;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageLog extends ListenerAdapter
{
	Logger logger = LoggerFactory.getLogger(MessageLog.class);
	MessageLogService service;
	
	public MessageLog()
	{
		service = RawrBot.applicationContext.getBean(MessageLogService.class);
	}
	
	private Role getUserLevel(Channel channel, User user)
	{
		if (channel.isOwner(user))
			return Role.OWNER;
		else if (channel.isSuperOp(user))
			return Role.SUPEROP;
		else if (channel.isOp(user))
			return Role.OP;
		else if (channel.isHalfOp(user))
			return Role.HALFOP;
		else if (channel.hasVoice(user))
			return Role.VOICE;
		else
			return Role.USER;
	}
	
	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		Date date = new Date();
		service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getMessage(), event
				.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
				getUserLevel(event.getChannel(), event.getUser()), MessageType.MESSAGE, date);
	}
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onPrivateMessage(event);
	}
	
	@Override
	public void onAction(ActionEvent event) throws Exception
	{
		Date date = new Date();
		service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getMessage(), event
				.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
				getUserLevel(event.getChannel(), event.getUser()), MessageType.ACTION, date);
	}
	
	@Override
	public void onNotice(NoticeEvent event) throws Exception
	{
		if (event.getChannel() != null) // Channel is somehow always null. Likely because a notice is a private message in some way.
		{
			Date date = new Date();
			service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getMessage(), event
					.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
					getUserLevel(event.getChannel(), event.getUser()), MessageType.NOTICE, date);
		}
	}
	
	@Override
	public void onJoin(JoinEvent event) throws Exception
	{
		if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			Date date = new Date();
			String message = event.getUser().getNick() + " (" + event.getUser().getLogin() + "@" + event.getUser().getHostmask()
					+ ") has joined " + event.getChannel().getName();
			service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
					.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
					getUserLevel(event.getChannel(), event.getUser()), MessageType.JOIN, date);
		}
	}
	
	@Override
	public void onPart(PartEvent event) throws Exception
	{
		if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			Date date = new Date();
			String message = event.getUser().getNick() + " (" + event.getUser().getLogin() + "@" + event.getUser().getHostmask()
					+ ") has left " + event.getChannel().getName() + ". (" + event.getReason() + ")";
			service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
					.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(), Role.USER, MessageType.PART, date);
		}
	}
	
	@Override
	public void onQuit(QuitEvent event) throws Exception
	{
		// String channels = "";
		// for (Channel channel : event.getDaoSnapshot().getChannels(event.getUser()))
		// {
		// if (event.getBot().getConfiguration().getAutoJoinChannels().containsValue(channel.toString()))
		// {
		// channels += channel.toString() + " ";
		// }
		// }
		if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			Date date = new Date();
			String message = event.getUser().getNick() + " (" + event.getUser().getLogin() + "@" + event.getUser().getHostmask()
					+ ") has quit. " + " (" + event.getReason() + ")";
			service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, "UNKNOWN", event
					.getBot().getConfiguration().getServerHostname(), Role.USER, MessageType.QUIT, date);
		}
	}
	
	@Override
	public void onKick(KickEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onKick(event);
	}
	
	@Override
	public void onNickChange(NickChangeEvent event) throws Exception
	{
		Date date = new Date();
	}
	
	@Override
	public void onOwner(OwnerEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onOwner(event);
	}
	
	@Override
	public void onSuperOp(SuperOpEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onSuperOp(event);
	}
	
	@Override
	public void onOp(OpEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onOp(event);
	}
	
	@Override
	public void onHalfOp(HalfOpEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onHalfOp(event);
	}
	
	@Override
	public void onVoice(VoiceEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onVoice(event);
	}
	
	@Override
	public void onMotd(MotdEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onMotd(event);
	}
	
	@Override
	public void onTopic(TopicEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onTopic(event);
	}
}