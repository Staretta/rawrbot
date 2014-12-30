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
	private boolean fromIdentified = false;
	private String channel = "";
	
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
	
	public long getId()
	{
		return id;
	}
	
	public String getToNickname()
	{
		return toNickname;
	}
	
	public String getMessage()
	{
		return message;
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
	
	public boolean isFromIdentified()
	{
		return fromIdentified;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public boolean isTold()
	{
		return told;
	}
	
	public Date getToldDate()
	{
		return toldDate;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public void setToNickname(String toNickname)
	{
		this.toNickname = toNickname;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
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
	
	public void setFromIdentified(boolean fromIdentified)
	{
		this.fromIdentified = fromIdentified;
	}
	
	public void setChannel(String channel)
	{
		this.channel = channel;
	}
	
	public void setServer(String server)
	{
		this.server = server;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public void setTold(boolean told)
	{
		this.told = told;
	}
	
	public void setToldDate(Date toldDate)
	{
		this.toldDate = toldDate;
	}
}
