package net.staretta.businesslogic.services;

import net.staretta.businesslogic.entity.SexDiceEntity;
import net.staretta.businesslogic.entity.SexDiceEntity.DiceType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SexDiceService extends BaseService
{
	public void addAction(String action)
	{
		Session session = getSession();
		SexDiceEntity entity = new SexDiceEntity(DiceType.ACTION, action);
		session.saveOrUpdate(entity);
	}
	
	public void addBodypart(String bodypart)
	{
		Session session = getSession();
		SexDiceEntity entity = new SexDiceEntity(DiceType.BODYPART, bodypart);
		session.saveOrUpdate(entity);
	}
	
	public void addLocation(String location)
	{
		Session session = getSession();
		SexDiceEntity entity = new SexDiceEntity(DiceType.LOCATION, location);
		session.saveOrUpdate(entity);
	}
	
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
}