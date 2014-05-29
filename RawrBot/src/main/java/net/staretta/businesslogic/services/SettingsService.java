package net.staretta.businesslogic.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.staretta.businesslogic.entity.Settings;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jpa.internal.EntityManagerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsService
{
	@PersistenceContext
	private EntityManager	em;

	private List<Settings> getBotSettings()
	{
		Session s = getSession(em);
		Query q = s.createQuery("select s from Settings s");
		return (List<Settings>) q.list();
	}

	private void addChannel(String channel, String server)
	{
		Session s = getSession(em);
		List<String> channels = getChannels(server);
		channels.add(channel);
		s.saveOrUpdate(channels);
	}

	private void removeChannel(String channel, String server)
	{
		Session s = getSession(em);

	}

	private List<String> getChannels(String server)
	{
		Session s = getSession(em);
		Query q = s.createQuery("select s.channels from Settings s where LOWER(s.server) = :server");
		q.setParameter("server", server.toLowerCase());
		return (List<String>) q.list();
	}

	private Session getSession(EntityManager em)
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}