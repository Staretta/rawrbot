package net.staretta.modules;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.staretta.RawrBot;
import net.staretta.businesslogic.entity.MessageLogEntity.MessageType;
import net.staretta.businesslogic.entity.MessageLogEntity.Role;
import net.staretta.businesslogic.services.MessageLogService;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSortedSet;

public class MessageLog extends ListenerAdapter
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private MessageLogService service;
	
	private Map<String, ImmutableSortedSet<Channel>> userMap = new HashMap<String, ImmutableSortedSet<Channel>>();
	
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
		userMap.put(event.getUser().getNick(), event.getUser().getChannels());
		Date date = new Date();
		service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getMessage(), event
				.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
				getUserLevel(event.getChannel(), event.getUser()), MessageType.MESSAGE, date);
	}
	
	// @Override
	// public void onPrivateMessage(PrivateMessageEvent event) throws Exception
	// {
	// // TODO Auto-generated method stub
	// super.onPrivateMessage(event);
	// }
	
	@Override
	public void onAction(ActionEvent event) throws Exception
	{
		userMap.put(event.getUser().getNick(), event.getUser().getChannels());
		Date date = new Date();
		service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getMessage(), event
				.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
				getUserLevel(event.getChannel(), event.getUser()), MessageType.ACTION, date);
	}
	
	@Override
	public void onJoin(JoinEvent event) throws Exception
	{
		
		if (event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			for (User user : event.getChannel().getUsers())
			{
				userMap.put(user.getNick(), event.getUser().getChannels());
			}
		}
		else if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			userMap.put(event.getUser().getNick(), event.getUser().getChannels());
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
			userMap.put(event.getUser().getNick(), event.getUser().getChannels());
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
		if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			String channels = "";
			for (Channel channel : userMap.get(event.getUser().getNick()))
			{
				if (event.getBot().getUserBot().getChannels().contains(channel))
				{
					channels += channel.getName() + " ";
				}
			}
			channels = channels.trim();
			
			Date date = new Date();
			String message = event.getUser().getNick() + " (" + event.getUser().getLogin() + "@" + event.getUser().getHostmask()
					+ ") has quit. " + " (" + event.getReason() + ")";
			service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, channels, event
					.getBot().getConfiguration().getServerHostname(), Role.USER, MessageType.QUIT, date);
		}
	}
	
	//
	// @Override
	// public void onKick(KickEvent event) throws Exception
	// {
	// // TODO Auto-generated method stub
	// super.onKick(event);
	// }
	//
	
	@Override
	public void onNickChange(NickChangeEvent event) throws Exception
	{
		if (!event.getOldNick().equalsIgnoreCase(event.getBot().getConfiguration().getName()))
		{
			String channels = "";
			if (userMap.containsKey(event.getNewNick()))
			{
				for (Channel channel : userMap.get(event.getNewNick()))
				{
					if (event.getBot().getUserBot().getChannels().contains(channel))
					{
						channels += channel.getName() + " ";
					}
				}
			}
			else
			{
				for (Channel channel : userMap.get(event.getOldNick()))
				{
					if (event.getBot().getUserBot().getChannels().contains(channel))
					{
						channels += channel.getName() + " ";
					}
				}
			}
			channels = channels.trim();
			
			Date date = new Date();
			String message = event.getOldNick() + " changed nickname to " + event.getNewNick();
			service.addLog(event.getOldNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, channels, event.getBot()
					.getConfiguration().getServerHostname(), Role.USER, MessageType.NICK, date);
		}
	}
	//
	// @Override
	// public void onOwner(OwnerEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became owner of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(), event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onSuperOp(SuperOpEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became super operator of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(), event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onOp(OpEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became operator of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(), event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onHalfOp(HalfOpEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became half operator of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(), event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onVoice(VoiceEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became voiced for channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(), event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onMotd(MotdEvent event) throws Exception
	// {
	// // TODO Auto-generated method stub
	// super.onMotd(event);
	// }
	//
	// @Override
	// public void onTopic(TopicEvent event) throws Exception
	// {
	// // TODO Auto-generated method stub
	// super.onTopic(event);
	// }
}