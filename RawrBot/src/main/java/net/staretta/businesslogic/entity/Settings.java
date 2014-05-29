package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "settings" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private Long				id;

	private String				nickname;
	private String				username;
	private String				version;
	private String				server;
	private String				port;
	private List<String>		channels;
	private String				password;

	public Settings()
	{

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

	public String getPort()
	{
		return port;
	}

	public List<String> getChannels()
	{
		return channels;
	}

	public void setChannels(List<String> channels)
	{
		this.channels = channels;
	}

	public String getPassword()
	{
		return password;
	}
}