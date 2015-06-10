package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jasypt.hibernate4.type.EncryptedStringType;

@Entity
@Table(name = "server_channels")
@TypeDef(name = "encryptedString", typeClass = EncryptedStringType.class, parameters = { @Parameter(name = "encryptorRegisteredName", value = "strongHibernateStringEncryptor") })
public class ChannelEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "server_channels" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String channel;
	@Type(type = "encryptedString")
	private String password;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "server_channel_modules", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "server_channel_modules")
	private Set<String> modules;
	@ManyToOne
	private ServerEntity servers;
	
	public ChannelEntity()
	{
		
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public void setChannel(String channel)
	{
		this.channel = channel;
	}
	
	public Set<String> getModules()
	{
		return modules;
	}
	
	public void setModules(Set<String> modules)
	{
		this.modules = modules;
	}
	
	public boolean hasPassword()
	{
		if (password != null)
			return true;
		return false;
	}
}
