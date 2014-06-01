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

	public List<Settings> getBotSettings()
	{
		Query q = getSession().createQuery("select s from Settings s");
		return (List<Settings>) q.list();
	}

	public void addChannel(String channel, String server)
	{
		Session s = getSession();
		Settings serverSettings = getServerSettings(server);
		List<String> channels = serverSettings.getChannels();
		channels.add(channel);
		serverSettings.setChannels(channels);
		s.saveOrUpdate(serverSettings);
	}

	public void removeChannel(String channel, String server)
	{
		Session s = getSession();
		Settings serverSettings = getServerSettings(server);
		List<String> channels = serverSettings.getChannels();
		channels.remove(channel);
		serverSettings.setChannels(channels);
		s.saveOrUpdate(serverSettings);
	}

	public List<String> getServerChannels(String server)
	{
		return getServerSettings(server).getChannels();
	}

	public Settings getServerSettings(String server)
	{
		Query q = getSession().createQuery("select s from Settings s where s.server = :server");
		q.setParameter("server", server.toLowerCase());
		return (Settings) q.uniqueResult();
	}

	public List<String> getServerModules(String server)
	{
		return getServerSettings(server).getModules();
	}

	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}