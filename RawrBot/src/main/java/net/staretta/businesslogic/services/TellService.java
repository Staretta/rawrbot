package net.staretta.businesslogic.services;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.staretta.businesslogic.entity.TellEntity;

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
public class TellService
{
	@PersistenceContext
	private EntityManager em;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public TellService()
	{
		
	}
	
	public boolean addTell(User user, String toNickname, String message, String server)
	{
		Date date = new Date();
		Session s = getSession();
		TellEntity tell = new TellEntity(user.getNick(), user.getRealName(), user.getHostmask(), toNickname, message, server, date);
		s.save(tell);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<TellEntity> getTells(String nickname, String server)
	{
		Query q = getSession().createQuery(
				"from TellEntity as tell where lower(tell.toNickname) = lower(:toNickname) and tell.told = false and tell.server = :server");
		q.setParameter("toNickname", nickname);
		q.setParameter("server", server);
		return (ArrayList<TellEntity>) q.list();
	}
	
	public void setTold(TellEntity entity)
	{
		getSession().saveOrUpdate(entity);
	}
	
	public ArrayList<TellEntity> getTolds(String nickname, String server)
	{
		return getTolds(nickname, server, 5);
	}
	
	public ArrayList<TellEntity> getTolds(String nickname, String server, int amount)
	{
		return null;
	}
	
	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}
