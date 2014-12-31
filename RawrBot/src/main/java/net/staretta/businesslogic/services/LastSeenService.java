package net.staretta.businesslogic.services;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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

	public boolean updateLastSeen(User user, String message, String server, String channel)
	{
		return true;
	}

	public LastSeenEntity getLastSeen(String nickname, String server)
	{
		TypedQuery<LastSeenEntity> query = em.createQuery("from LastSeen as seen where lower(seen.nickname) = lower(:nickname) "
				+ "and seen.server = :server order by seen.id desc", LastSeenEntity.class);
		query.setParameter("nickname", nickname);
		query.setParameter("server", server);
		return query.getSingleResult();
	}

	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}
