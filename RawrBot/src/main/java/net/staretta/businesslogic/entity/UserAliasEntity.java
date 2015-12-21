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
@Table(name = "user_aliases")
public class UserAliasEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "user_aliases" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String nickname;
	@ManyToOne
	private UserEntity user;
	
	public UserAliasEntity()
	{
	}
	
	public long getId()
	{
		return id;
	}
	
	public String getNickname()
	{
		return nickname;
	}
	
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
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
