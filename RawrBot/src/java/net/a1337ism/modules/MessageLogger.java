package net.a1337ism.modules;

import java.util.Date;

import net.a1337ism.RawrBot;
import net.a1337ism.database.HibernateSession;
import net.a1337ism.database.MessageLoggerDbModel;

import org.hibernate.Session;
import org.pircbotx.UserLevel;
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

import com.google.common.collect.ImmutableSortedSet;

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
			String type, String role)
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
		msgLog.setRole(role);
		session.save(msgLog);
		session.getTransaction().commit();
	}

	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getChannel().getUserLevels(event.getUser());
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(),
				event.getMessage(), event.getChannel().getName(), date, "MESSAGE", userLevel.toString());
	}

	@Override
	public void onAction(ActionEvent event) throws Exception
	{
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getChannel().getUserLevels(event.getUser());
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(),
				event.getMessage(), event.getChannel().getName(), date, "ACTION", userLevel.toString());
	}

	@Override
	public void onJoin(JoinEvent event) throws Exception
	{
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getChannel().getUserLevels(event.getUser());
		String message = event.getUser().getNick() + " joined channel " + event.getChannel().getName();
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
				.getChannel().getName(), date, "JOIN", userLevel.toString());
	}

	@Override
	public void onKick(KickEvent event) throws Exception
	{
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getChannel().getUserLevels(event.getUser());
		String message = event.getUser().getNick() + " has kicked " + event.getRecipient().getNick() + " from "
				+ event.getChannel().getName() + " (" + event.getReason() + ")";
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
				.getChannel().getName(), date, "JOIN", userLevel.toString());
	}

	@Override
	public void onNickChange(NickChangeEvent event) throws Exception
	{
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getUser().getUserLevels(
				event.getBot().getUserBot().getChannels().first());
		String message = event.getOldNick() + " is now known as " + event.getNewNick();
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
				.getBot().getUserBot().getChannels().first().toString(), date, "JOIN", userLevel.toString());
	}

	@Override
	public void onNotice(NoticeEvent event) throws Exception
	{
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getChannel().getUserLevels(event.getUser());
		String message = "-" + event.getUser().getNick() + "- " + event.getNotice();
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
				.getChannel().getName(), date, "JOIN", userLevel.toString());
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
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getChannel().getUserLevels(event.getUser());
		String message = event.getUser().getNick() + " has left.";
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
				.getChannel().getName(), date, "JOIN", userLevel.toString());
	}

	@Override
	public void onQuit(QuitEvent event) throws Exception
	{
		Date date = new Date();
		ImmutableSortedSet<UserLevel> userLevel = event.getUser().getUserLevels(
				event.getBot().getUserBot().getChannels().first());
		String message = event.getUser().getNick() + " has quit. (" + event.getReason() + ")";
		addLog(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), message, event
				.getBot().getUserBot().getChannels().first().toString(), date, "JOIN", userLevel.toString());
	}

	@Override
	public void onVoice(VoiceEvent event) throws Exception
	{
		// TODO Auto-generated method stub
		super.onVoice(event);
	}

}