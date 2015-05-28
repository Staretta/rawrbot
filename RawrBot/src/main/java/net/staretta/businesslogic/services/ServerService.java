package net.staretta.businesslogic.services;

import java.util.List;

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
		List<String> modules = getChannelModules(server, channel);
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
		return getServerSettings(server).getGlobalConfig();
	}
	
	public List<String> getChannelModules(String server, String channel)
	{
		return getServerChannel(server, channel).getModules();
	}
	
	public ChannelEntity getServerChannel(String server, String channel)
	{
		List<ChannelEntity> channels = getServerChannels(server);
		for (ChannelEntity chan : channels)
		{
			if (chan.getChannel().equalsIgnoreCase(channel))
			{
				return chan;
			}
		}
		return null;
	}
	
	public List<ChannelEntity> getServerChannels(String server)
	{
		return getServerSettings(server).getChannels();
	}
	
	public ServerEntity getServerSettings(String server)
	{
		Query q = getSession().createQuery("from ServerEntity as s where s.server = :server");
		q.setParameter("server", server.toLowerCase());
		return (ServerEntity) q.uniqueResult();
	}
}