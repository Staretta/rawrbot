package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tell")
public class TellEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "tell" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;

	private String toNickname;
	@Column(length = 1000)
	private String message;

	private String fromNickname;
	private String fromUsername;
	private String fromHostmask;

	private String server;

	@Column(name = "date", columnDefinition = "timestamp with time zone")
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private boolean told = false;

	@Column(name = "tolddate", columnDefinition = "timestamp with time zone")
	@Temporal(TemporalType.TIMESTAMP)
	private Date toldDate;

	public TellEntity()
	{

	}

	public TellEntity(String fromNickname, String fromUsername, String fromHostmask, String toNickname, String message,
			String server, Date date)
	{
		this.toNickname = toNickname;
		this.fromNickname = fromNickname;
		this.fromUsername = fromUsername;
		this.fromHostmask = fromHostmask;
		this.message = message;
		this.server = server;
		this.date = date;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public boolean isTold()
	{
		return told;
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

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public void setTold(boolean told)
	{
		this.told = told;
	}

	public String getToNickname()
	{
		return toNickname;
	}

	public String getFromNickname()
	{
		return fromNickname;
	}

	public String getFromUsername()
	{
		return fromUsername;
	}

	public String getFromHostmask()
	{
		return fromHostmask;
	}

	public void setToNickname(String toNickname)
	{
		this.toNickname = toNickname;
	}

	public void setFromNickname(String fromNickname)
	{
		this.fromNickname = fromNickname;
	}

	public void setFromUsername(String fromUsername)
	{
		this.fromUsername = fromUsername;
	}

	public void setFromHostmask(String fromHostmask)
	{
		this.fromHostmask = fromHostmask;
	}
}
