package net.staretta.businesslogic.services;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.staretta.businesslogic.entity.LastSeenEntity;

import org.hibernate.Session;
import org.hibernate.jpa.internal.EntityManagerImpl;
import org.pircbotx.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LastSeenService
{
	@PersistenceContext
	private EntityManager em;

	Logger logger = LoggerFactory.getLogger(getClass());

	public LastSeenService()
	{

	}

	public boolean addLastSeen(User user, String message, String server, String channel)
	{
		Date date = new Date();
		Session s = getSession();
		LastSeenEntity seen = new LastSeenEntity();
		seen.setNickname(user.getNick());
		seen.setHostmask(user.getHostmask());
		seen.setUsername(user.getLogin());
		seen.setMessage(message);
		seen.setServer(server);
		seen.setChannel(channel);
		seen.setDate(date);
		s.save(seen);
		return true;
	}

	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}
