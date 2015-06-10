package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "servers")
public class ServerEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "servers" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String nickname;
	private String username;
	@Column(length = 512)
	private String version;
	private String server;
	private int port;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "servers")
	private Set<ChannelEntity> channels;
	private String password;
	private boolean ssl;
	@ManyToOne(fetch = FetchType.EAGER)
	private GlobalConfigEntity config;
	
	public ServerEntity()
	{
		
	}
	
	public Set<ChannelEntity> getChannels()
	{
		return channels;
	}
	
	public void setChannels(Set<ChannelEntity> channels)
	{
		this.channels = channels;
	}
	
	public boolean isSsl()
	{
		return ssl;
	}
	
	public Long getId()
	{
		return id;
	}
	
	public String getNickname()
	{
		return nickname;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public GlobalConfigEntity getGlobalConfig()
	{
		return config;
	}
	
	public void setGlobalConfig(GlobalConfigEntity config)
	{
		this.config = config;
	}
}