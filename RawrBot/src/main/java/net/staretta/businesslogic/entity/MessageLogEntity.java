package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "messagelog")
public class MessageLogEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "messagelog" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String nickname;
	private String username;
	private String hostmask;
	@Column(length = 512)
	private String message;
	private String server;
	private String channel;
	@Enumerated(EnumType.STRING)
	private Role role;
	@Enumerated(EnumType.STRING)
	private MessageType messageType;
	@Column(name = "date", columnDefinition = "timestamp with time zone")
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public MessageLogEntity()
	{

	}

	public MessageLogEntity(String nickname, String username, String hostmask, String message, String channel, String server, Role role,
			MessageType messageType, Date date)
	{
		this.nickname = nickname;
		this.username = username;
		this.hostmask = hostmask;
		this.message = message;
		this.channel = channel;
		this.server = server;
		this.role = role;
		this.messageType = messageType;
		this.date = date;
	}

	public static enum Role
	{
		USER, VOICE, HALFOP, OP, SUPEROP, OWNER, IRCOP
	}

	public static enum MessageType
	{
		MESSAGE, PRIVATE, ACTION, NOTICE, JOIN, PART, QUIT, KICK, NICK
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getHostmask()
	{
		return hostmask;
	}

	public void setHostmask(String hostmask)
	{
		this.hostmask = hostmask;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public MessageType getMessageType()
	{
		return messageType;
	}

	public void setMessageType(MessageType messageType)
	{
		this.messageType = messageType;
	}
}