package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "global_config")
public class GlobalConfigEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "global_config" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "config")
	private List<ServerEntity> servers;
	private String youtubeApiKey = "";
	private String smtpHost = "";
	private String smtpUsername = "";
	private String smtpPassword = "";
	private boolean smtpAuth = true;
	private boolean smtpSsl = true;
	private String smtpPort = "";
	
	public GlobalConfigEntity()
	{
	}
	
	public String getYoutubeApiKey()
	{
		return youtubeApiKey;
	}
	
	public void setYoutubeApiKey(String youtubeApiKey)
	{
		this.youtubeApiKey = youtubeApiKey;
	}
	
	public String getSmtpHost()
	{
		return smtpHost;
	}
	
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}
	
	public String getSmtpUsername()
	{
		return smtpUsername;
	}
	
	public void setSmtpUsername(String smtpUsername)
	{
		this.smtpUsername = smtpUsername;
	}
	
	public String getSmtpPassword()
	{
		return smtpPassword;
	}
	
	public void setSmtpPassword(String smtpPassword)
	{
		this.smtpPassword = smtpPassword;
	}
	
	public boolean isSmtpAuth()
	{
		return smtpAuth;
	}
	
	public void setSmtpAuth(boolean smtpAuth)
	{
		this.smtpAuth = smtpAuth;
	}
	
	public boolean isSmtpSsl()
	{
		return smtpSsl;
	}
	
	public void setSmtpSsl(boolean smtpSsl)
	{
		this.smtpSsl = smtpSsl;
	}
	
	public String getSmtpPort()
	{
		return smtpPort;
	}
	
	public void setSmtpPort(String smtpPort)
	{
		this.smtpPort = smtpPort;
	}
}
