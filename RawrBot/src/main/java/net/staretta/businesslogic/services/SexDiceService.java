package net.staretta.businesslogic.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.staretta.businesslogic.entity.SexDiceEntity;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jpa.internal.EntityManagerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SexDiceService
{
	@PersistenceContext
	private EntityManager em;
	
	public String getRandomAction()
	{
		Query q = getSession().createQuery("SELECT s.message FROM SexDiceEntity s WHERE s.diceType = :diceType ORDER BY RANDOM()")
				.setMaxResults(1);
		q.setParameter("diceType", SexDiceEntity.DiceType.ACTION);
		return q.uniqueResult().toString();
	}
	
	public String getRandomBodypart()
	{
		Query q = getSession().createQuery("SELECT s.message FROM SexDiceEntity s WHERE s.diceType = :diceType ORDER BY RANDOM()")
				.setMaxResults(1);
		q.setParameter("diceType", SexDiceEntity.DiceType.BODYPART);
		return q.uniqueResult().toString();
	}
	
	public String getRandomLocation()
	{
		Query q = getSession().createQuery("SELECT s.message FROM SexDiceEntity s WHERE s.diceType = :diceType ORDER BY RANDOM()")
				.setMaxResults(1);
		q.setParameter("diceType", SexDiceEntity.DiceType.LOCATION);
		return q.uniqueResult().toString();
	}
	
	public String getRandomSexDice()
	{
		return getRandomAction() + " " + getRandomBodypart() + " " + getRandomLocation();
	}
	
	public Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}