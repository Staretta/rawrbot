package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "settings" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String nickname;
	private String username;
	@Column(length = 512)
	private String version;
	private String server;
	private int port;
	// @ElementCollection(fetch = FetchType.EAGER)
	// @CollectionTable(name = "channels", joinColumns = @JoinColumn(name = "id"))
	// @Column(name = "channels")
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "settings")
	private List<ChannelEntity> channels;
	// @ElementCollection(fetch = FetchType.EAGER)
	// @CollectionTable(name = "modules", joinColumns = @JoinColumn(name = "id"))
	// @Column(name = "modules")
	// private List<String> modules;
	private String password;
	private boolean ssl;

	public Settings()
	{

	}

	// public List<String> getModules()
	// {
	// return modules;
	// }
	//
	// public void setModules(List<String> modules)
	// {
	// this.modules = modules;
	// }

	public List<ChannelEntity> getChannels()
	{
		return channels;
	}

	public void setChannels(List<ChannelEntity> channels)
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
}