package net.staretta.modules;

import java.util.Date;

import net.staretta.RawrBot;
import net.staretta.businesslogic.entity.MessageLogEntity;
import net.staretta.businesslogic.services.MessageLogService;

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
	
	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		Date date = new Date();
		logger.info(event.getUser().getUserLevels(event.getChannel()).toString());
		service.addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getMessage(), event
				.getChannel().getName(), event.getBot().getConfiguration().getServerHostname(), MessageLogEntity.Role.USER,
				MessageLogEntity.MessageType.MESSAGE, date);
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
		// TODO Auto-generated method stub
		super.onAction(event);
	}
	
	@Override
	public void onNotice(NoticeEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onNotice(event);
	}
	
	@Override
	public void onJoin(JoinEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onJoin(event);
	}
	
	@Override
	public void onPart(PartEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onPart(event);
	}
	
	@Override
	public void onQuit(QuitEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onQuit(event);
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
		// TODO Auto-generated method stub
		super.onNickChange(event);
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