package net.staretta.businesslogic.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "user_channels")
public class UserChannelEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "user_channels" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String channel;
	@ManyToOne
	UserEntity user;
	
	public UserChannelEntity()
	{
	}
	
	public UserChannelEntity(String channel, UserEntity user)
	{
		this.channel = channel;
		this.user = user;
	}
	
	public long getId()
	{
		return id;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public void setChannel(String channel)
	{
		this.channel = channel;
	}
	
	public UserEntity getUser()
	{
		return user;
	}
	
	public void setUser(UserEntity user)
	{
		this.user = user;
	}
}
