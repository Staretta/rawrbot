package net.staretta.businesslogic.services;

import java.util.ArrayList;
import java.util.Date;

import net.staretta.businesslogic.entity.LastSeenEntity;
import net.staretta.businesslogic.entity.LastSeenEntity.MessageType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.pircbotx.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LastSeenService extends BaseService
{
	public LastSeenService()
	{
		
	}
	
	public boolean addLastSeen(User user, String message, String server, String channel, MessageType messageType)
	{
		Session s = getSession();
		LastSeenEntity seen = new LastSeenEntity();
		seen.setNickname(user.getNick());
		seen.setHostmask(user.getHostmask());
		seen.setUsername(user.getLogin());
		seen.setMessage(message);
		seen.setServer(server);
		seen.setChannel(channel);
		seen.setDate(new Date());
		seen.setMessageType(messageType);
		s.save(seen);
		return true;
	}
	
	public boolean updateLastSeen(LastSeenEntity seen)
	{
		getSession().update(seen);
		return true;
	}
	
	public LastSeenEntity getLastSeen(String nickname, String server)
	{
		Query query = getSession().createQuery(
				"from LastSeenEntity as seen where lower(seen.nickname) = lower(:nickname) and seen.server = :server");
		query.setParameter("nickname", nickname);
		query.setParameter("server", server);
		return (LastSeenEntity) query.uniqueResult();
	}
	
	public ArrayList<LastSeenEntity> getLastSeenLike(String nickname, String server)
	{
		Query query = getSession().createQuery(
				"from LastSeenEntity as seen where lower(seen.nickname) = lower(:nickname) and seen.server = :server");
		query.setParameter("nickname", "%" + nickname + "%");
		query.setParameter("server", server);
		query.setMaxResults(3);
		return (ArrayList<LastSeenEntity>) query.list();
	}
}
