package net.staretta.businesslogic.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.staretta.businesslogic.entity.MessageLogEntity;
import net.staretta.businesslogic.entity.MessageLogEntity.MessageType;
import net.staretta.businesslogic.entity.MessageLogEntity.Role;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jpa.internal.EntityManagerImpl;
import org.pircbotx.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageLogService
{
	@PersistenceContext
	private EntityManager em;

	Logger logger = LoggerFactory.getLogger(getClass());

	public MessageLogService()
	{

	}

	public void addLog(User user, String message, String channel, String server, Role role, MessageType messageType)
	{
		Session s = getSession();
		MessageLogEntity log = new MessageLogEntity();
		log.setUsername(user.getLogin());
		log.setNickname(user.getNick());
		log.setHostmask(user.getHostmask());
		log.setServer(server);
		log.setChannel(channel);
		log.setRole(role);
		log.setMessage(message);
		log.setMessageType(messageType);
		log.setDate(new Date());
		s.saveOrUpdate(log);
	}

	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}

	public boolean isSpam(User user, String message, String server, int limit, int minutes)
	{
		// Set endDate to current, and start date to minutes ago.
		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.MINUTE, -minutes);
		Date startDate = c.getTime();

		Query query = getSession().createQuery(
				"from MessageLogEntity as log where log.nickname = :nickname and log.server = :server "
						+ "and log.date between :startDate and :endDate and log.message = :message order by log.id desc");
		query.setParameter("nickname", user.getNick());
		query.setParameter("server", server);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("message", message);

		List<MessageLogEntity> list = (List<MessageLogEntity>) query.list();

		if (list.size() >= limit)
			return true;
		else
			return false;
	}
}