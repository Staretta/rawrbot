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
	
	public TellEntity(String fromNickname, String fromUsername, String fromHostmask, String toNickname, String message, String server,
			Date date, String channel)
	{
		this.toNickname = toNickname;
		this.fromNickname = fromNickname;
		this.fromUsername = fromUsername;
		this.fromHostmask = fromHostmask;
		this.message = message;
		this.server = server;
		this.date = date;
		this.channel = channel;
	}
	
	public Date getToldDate()
	{
		return toldDate;
	}
	
	public void setToldDate(Date toldDate)
	{
		this.toldDate = toldDate;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public boolean isFromIdentified()
	{
		return fromIdentified;
	}
	
	public boolean isTold()
	{
		return told;
	}
	
	public void setTold(boolean told)
	{
		this.told = told;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public Date getDate()
	{
		return date;
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
}
