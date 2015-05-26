package net.staretta.businesslogic.services;

import net.staretta.businesslogic.entity.EightballEntity;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EightballService extends BaseService
{
	public void addAnswer(String answer)
	{
		Session session = getSession();
		EightballEntity entity = new EightballEntity(answer);
		session.saveOrUpdate(entity);
	}
	
	public void removeAnswer()
	{
		
	}
	
	public String getRandomAnswer()
	{
		Query q = getSession().createQuery("SELECT e.message FROM EightballEntity e ORDER BY RANDOM()").setMaxResults(1);
		return q.uniqueResult().toString();
	}
}