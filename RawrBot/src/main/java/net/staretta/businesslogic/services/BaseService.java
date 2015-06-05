package net.staretta.businesslogic.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.jpa.internal.EntityManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseService
{
	@PersistenceContext
	private EntityManager em;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public BaseService()
	{
	}
	
	protected Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
	
	protected EntityManager getEntityManager()
	{
		return em;
	}
	
	public Logger getLogger()
	{
		return logger;
	}
}
