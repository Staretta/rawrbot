package net.staretta.businesslogic.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jpa.internal.EntityManagerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EightballAnswersService
{
	@PersistenceContext
	private EntityManager	em;

	public void addAnswer()
	{

	}

	public void removeAnswer()
	{

	}

	public void getRandomAnswer()
	{
		Query q = getSession().createQuery("");

	}

	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}