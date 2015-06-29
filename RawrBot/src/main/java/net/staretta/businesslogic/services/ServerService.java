package net.staretta.businesslogic.services;

import java.util.List;
import java.util.Set;

import net.staretta.businesslogic.entity.ChannelEntity;
import net.staretta.businesslogic.entity.GlobalConfigEntity;
import net.staretta.businesslogic.entity.ServerEntity;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServerService extends BaseService
{
	public List<ServerEntity> getBotSettings()
	{
		Query q = getSession().createQuery("select s from ServerEntity s");
		return (List<ServerEntity>) q.list();
	}
	
	// public void addChannel(String channel, String server)
	// {
	// Session s = getSession();
	// Settings serverSettings = getServerSettings(server);
	// List<String> channels = serverSettings.getChannels();
	// channels.add(channel);
	// serverSettings.setChannels(channels);
	// s.saveOrUpdate(serverSettings);
	// }
	//
	// public void removeChannel(String channel, String server)
	// {
	// Session s = getSession();
	// Settings serverSettings = getServerSettings(server);
	// List<String> channels = serverSettings.getChannels();
	// channels.remove(channel);
	// serverSettings.setChannels(channels);
	// s.saveOrUpdate(serverSettings);
	// }
	
	public boolean hasChannelModule(String server, String channel, String module)
	{
		Set<String> modules = getChannelModules(server, channel);
		for (String m : modules)
		{
			if (m.equalsIgnoreCase(module))
			{
				return true;
			}
		}
		return false;
	}
	
	public GlobalConfigEntity getGlobalConfig(String server)
	{
		ServerEntity serverEntity = getServerSettings(server);
		if (server != null)
			return serverEntity.getGlobalConfig();
		return null;
	}
	
	public Set<String> getChannelModules(String server, String channel)
	{
		ChannelEntity channelEntity = getServerChannel(server, channel);
		if (channelEntity != null)
			return channelEntity.getModules();
		return null;
	}
	
	public ChannelEntity getServerChannel(String server, String channel)
	{
		Set<ChannelEntity> channels = getServerChannels(server);
		for (ChannelEntity chan : channels)
		{
			if (chan.getChannel().equalsIgnoreCase(channel))
			{
				return chan;
			}
		}
		return null;
	}
	
	public Set<ChannelEntity> getServerChannels(String server)
	{
		ServerEntity serverEntity = getServerSettings(server);
		if (server != null)
			return serverEntity.getChannels();
		return null;
	}
	
	public ServerEntity getServerSettings(String server)
	{
		Query q = getSession().createQuery("from ServerEntity as s where s.server = :server");
		q.setParameter("server", server.toLowerCase());
		return (ServerEntity) q.uniqueResult();
	}
}