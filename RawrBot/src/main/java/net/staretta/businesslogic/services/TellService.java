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
	
	public boolean addTell(String toNickname, User user, String message, String server)
	{
		Date date = new Date();
		Session s = getSession();
		TellEntity tell = new TellEntity(toNickname, user.getNick(), user.getRealName(), user.getHostmask(), message, server, date);
		s.save(tell);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<TellEntity> getTells(String nickname, String server)
	{
		Query q = getSession().createQuery(
				"SELECT t FROM TellEntity t WHERE t.toNickname LIKE :toNickname AND t.told = false AND t.server = :server");
		q.setParameter("toNickname", nickname);
		q.setParameter("server", server);
		return (ArrayList<TellEntity>) q.list();
	}
	
	public void setTold(TellEntity entity)
	{
		getSession().saveOrUpdate(entity);
	}
	
	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}
