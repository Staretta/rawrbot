package net.a1337ism.modules;

import java.util.Date;

import net.a1337ism.RawrBot;
import net.a1337ism.database.HibernateSession;
import net.a1337ism.database.MessageLoggerDbModel;

import org.hibernate.Session;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageLogger extends ListenerAdapter
{
	private static Logger	logger;
	MessageLoggerDbModel	db;
	HibernateSession		hibernateConnection;

	public MessageLogger()
	{
		this.logger = LoggerFactory.getLogger(RawrBot.class);
		this.db = new MessageLoggerDbModel();
		this.hibernateConnection = new HibernateSession();
	}

	public void addLog(String nickname, String username, String hostmask, String message, String channel, Date date,
			String type)
	{
		Session session = HibernateSession.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		MessageLoggerDbModel msgLog = new MessageLoggerDbModel();
		msgLog.setNickname(nickname);
		msgLog.setChannel(channel);
		msgLog.setMessage(message);
		msgLog.setUsername(username);
		msgLog.setHostmask(hostmask);
		msgLog.setTime(date);
		msgLog.setType(type);
		session.save(msgLog);
		session.getTransaction().commit();
	}

	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		Date date = new Date();
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(),
				event.getMessage(), event.getChannel().getName(), date, "MESSAGE");
	}

	@Override
	public void onAction(ActionEvent event) throws Exception
	{
		Date date = new Date();
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(),
				event.getMessage(), event.getChannel().getName(), date, "ACTION");
	}

	@Override
	public void onJoin(JoinEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onJoin(event);
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
	public void onNotice(NoticeEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onNotice(event);
	}

	@Override
	public void onOp(OpEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onOp(event);
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
	public void onVoice(VoiceEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onVoice(event);
	}

}