package net.staretta.businesslogic.services;

import java.util.ArrayList;
import java.util.Date;

import net.staretta.businesslogic.entity.TellEntity;

import org.hibernate.Query;
import org.hibernate.Session;
import org.pircbotx.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TellService extends BaseService
{
	public TellService()
	{
		
	}
	
	public boolean addTell(User user, String toNickname, String message, String server, String channel)
	{
		Session s = getSession();
		TellEntity tell = new TellEntity();
		tell.setFromNickname(user.getNick());
		tell.setFromUsername(user.getLogin());
		tell.setFromHostmask(user.getHostmask());
		tell.setToNickname(toNickname);
		tell.setMessage(message);
		tell.setChannel(channel);
		tell.setServer(server);
		tell.setDate(new Date());
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
	public ArrayList<TellEntity> getTolds(String fromNickname, String[] toNickname, String server, int amount, Date[] dates)
	{
		// Create the string for the query.
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("from TellEntity as tell where lower(tell.fromNickname) = lower(:fromNickname) ");
		
		if (toNickname != null)
		{
			queryBuilder.append("and lower(tell.toNickname) = lower(:toNickname) ");
		}
		
		if (dates != null)
		{
			queryBuilder.append("and tell.date between :startDate and :endDate ");
		}
		
		queryBuilder.append("and tell.server = :server order by tell.id desc");
		
		// Pass our created string to generator our query, and set parameters.
		Query q = getSession().createQuery(queryBuilder.toString());
		
		if (toNickname != null)
		{
			q.setParameter("toNickname", toNickname[0]);
		}
		
		if (dates != null)
		{
			q.setParameter("startDate", dates[0]);
			q.setParameter("endDate", dates[1]);
		}
		
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
}
