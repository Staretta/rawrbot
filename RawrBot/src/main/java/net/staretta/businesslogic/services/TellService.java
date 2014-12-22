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
	
	// Gets a joined / new messaged users current tells.
	@SuppressWarnings("unchecked")
	public ArrayList<TellEntity> getTells(String nickname, String server)
	{
		Query q = getSession()
				.createQuery(
						"from TellEntity as tell where lower(tell.toNickname) = lower(:toNickname) and tell.told = false and tell.server = :server");
		q.setParameter("toNickname", nickname);
		q.setParameter("server", server);
		return (ArrayList<TellEntity>) q.list();
	}
	
	public void setTold(TellEntity entity)
	{
		getSession().saveOrUpdate(entity);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<TellEntity> getTolds(String fromNickname, String toNickname, String server, int amount, Date[] dates)
	{
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("from TellEntity as tell where lower(tell.fromNickname) = lower(:fromNickname) ");
		
		if (toNickname != null)
			queryBuilder.append("and lower(tell.toNickname) = lower(:toNickname) ");
		
		if (dates != null)
		{
			if (dates.length == 2)
			{
				queryBuilder.append("and tell.date between :startDate and :endDate ");
			}
			else
			{
				queryBuilder.append("and tell.date = :date ");
			}
		}
		
		queryBuilder.append("and tell.server = :server order by tell.id desc");
		
		Query q = getSession().createQuery(queryBuilder.toString());
		if (toNickname != null)
			q.setParameter("toNickname", toNickname);
		if (dates != null)
			if (dates.length == 2)
			{
				q.setParameter("startDate", dates[0]);
				q.setParameter("endDate", dates[1]);
			}
			else
				q.setParameter("date", dates[0]);
		q.setParameter("server", server);
		q.setParameter("fromNickname", fromNickname);
		q.setMaxResults(amount);
		return (ArrayList<TellEntity>) q.list();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<TellEntity> getAllTolds(String fromNickname, String server)
	{
		Query q = getSession().createQuery(
				"from TellEntity as tell where lower(tell.fromNickname) = lower(:fromNickname) "
						+ "and tell.server = :server order by tell.id desc");
		q.setParameter("server", server);
		q.setParameter("fromNickname", fromNickname);
		q.setMaxResults(50);
		return (ArrayList<TellEntity>) q.list();
	}
	
	public boolean isVerified(String fromNickname, String server)
	{
		Query q = getSession().createQuery(
				"select told from TellEntity tell where lower(tell.fromNickname) = lower(:fromNickname)"
						+ " and tell.server = :server order by tell.id desc");
		q.setParameter("server", server);
		q.setParameter("fromNickname", fromNickname);
		return (boolean) q.uniqueResult();
	}
	
	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}
