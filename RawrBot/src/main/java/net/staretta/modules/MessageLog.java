package net.staretta.modules;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.entity.MessageLogEntity.MessageType;
import net.staretta.businesslogic.entity.MessageLogEntity.Role;
import net.staretta.businesslogic.services.MessageLogService;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.TopicEvent;

public class MessageLog extends BaseListener
{
	private MessageLogService service;
	
	public MessageLog()
	{
		service = RawrBot.getAppCtx().getBean(MessageLogService.class);
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setName("MessageLog");
		moduleInfo.setVersion("v0.9");
		return moduleInfo;
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
	public void onMessage(MessageEvent<PircBotX> event) throws Exception
	{
		service.addLog(event.getUser(), event.getMessage(), event.getChannel().getName(), event.getBot().getConfiguration()
				.getServerHostname(), getUserLevel(event.getChannel(), event.getUser()), MessageType.MESSAGE);
	}
	
	@Override
	public void onAction(ActionEvent<PircBotX> event) throws Exception
	{
		service.addLog(event.getUser(), event.getMessage(), event.getChannel().getName(), event.getBot().getConfiguration()
				.getServerHostname(), getUserLevel(event.getChannel(), event.getUser()), MessageType.ACTION);
	}
	
	@Override
	public void onJoin(JoinEvent<PircBotX> event) throws Exception
	{
		if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			String message = event.getUser().getNick() + " (" + event.getUser().getLogin() + "@" + event.getUser().getHostmask()
					+ ") has joined " + event.getChannel().getName();
			service.addLog(event.getUser(), message, event.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
					getUserLevel(event.getChannel(), event.getUser()), MessageType.JOIN);
		}
	}
	
	@Override
	public void onPart(PartEvent<PircBotX> event) throws Exception
	{
		if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			String message = event.getUser().getNick() + " (" + event.getUser().getLogin() + "@" + event.getUser().getHostmask()
					+ ") has left " + event.getChannel().getName() + ". (" + event.getReason() + ")";
			service.addLog(event.getUser(), message, event.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
					Role.USER, MessageType.PART);
		}
	}
	
	@Override
	public void onQuit(QuitEvent<PircBotX> event) throws Exception
	{
		if (!event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick()))
		{
			String message = event.getUser().getNick() + " (" + event.getUser().getLogin() + "@" + event.getUser().getHostmask()
					+ ") has quit. " + " (" + event.getReason() + ")";
			// Using SERVER as channel name because this is a server wide event and not a channel specific event.
			service.addLog(event.getUser(), message, "SERVER", event.getBot().getConfiguration().getServerHostname(), Role.USER,
					MessageType.QUIT);
		}
	}
	
	@Override
	public void onKick(KickEvent<PircBotX> event) throws Exception
	{
		if (!event.getRecipient().getNick().equalsIgnoreCase(event.getBot().getConfiguration().getName()))
		{
			String message = event.getUser().getNick() + " kicked " + event.getRecipient().getNick() + " from "
					+ event.getChannel().getName();
			service.addLog(event.getUser(), message, event.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(),
					Role.USER, MessageType.KICK);
		}
	}
	
	@Override
	public void onNickChange(NickChangeEvent<PircBotX> event) throws Exception
	{
		if (!event.getOldNick().equalsIgnoreCase(event.getBot().getConfiguration().getName()))
		{
			String message = event.getOldNick() + " changed nickname to " + event.getNewNick();
			// Using SERVER as channel name because this is a server wide event and not a channel specific event.
			service.addLog(event.getUser(), message, "SERVER", event.getBot().getConfiguration().getServerHostname(), Role.USER,
					MessageType.NICK);
		}
	}
	
	@Override
	public void onTopic(TopicEvent<PircBotX> event) throws Exception
	{
		// Do stuff here?
	}
	
	@Override
	public void OnMessage(MessageEvent<PircBotX> event)
	{
		// We don't care about Capital OnMessage, only want to use lowercase onMessage.
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent<PircBotX> event)
	{
		// We don't care about Capital OnPrivateMessage, only want to use lowercase onPrivateMessage.
	}
	
	//
	// @Override
	// public void onOwner(OwnerEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became owner of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message,
	// event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(),
	// event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onSuperOp(SuperOpEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became super operator of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message,
	// event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(),
	// event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onOp(OpEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became operator of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message,
	// event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(),
	// event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onHalfOp(HalfOpEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became half operator of channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message,
	// event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(),
	// event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	//
	// @Override
	// public void onVoice(VoiceEvent event) throws Exception
	// {
	// Date date = new Date();
	// String message = event.getRecipient() + " became voiced for channel " + event.getChannel().getName();
	// service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message,
	// event.getChannel()
	// .getName(), event.getBot().getConfiguration().getServerHostname(), getUserLevel(event.getChannel(),
	// event.getUser()),
	// MessageType.MESSAGE, date);
	// }
	
}