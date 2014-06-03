package net.staretta.businesslogic.services;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.staretta.businesslogic.entity.MessageLogEntity;
import net.staretta.businesslogic.entity.MessageLogEntity.MessageType;
import net.staretta.businesslogic.entity.MessageLogEntity.Role;

import org.hibernate.Session;
import org.hibernate.jpa.internal.EntityManagerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageLogService
{
	@PersistenceContext
	private EntityManager em;
	
	public MessageLogService()
	{
		
	}
	
	public void addLog(String nickname, String username, String hostmask, String message, String channel, String server, Role role,
			MessageType messageType, Date date)
	{
		Session s = getSession();
		MessageLogEntity log = new MessageLogEntity(nickname, username, hostmask, message, channel, server, role, messageType, date);
		s.saveOrUpdate(log);
	}
	
	private Session getSession()
	{
		return em.unwrap(EntityManagerImpl.class).getSession();
	}
}