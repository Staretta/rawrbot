package net.staretta.businesslogic.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "karma", indexes = { @Index(columnList = "giver", name = "giver_index"),
		@Index(columnList = "receiver", name = "receiver_index")})
public class KarmaEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "karma" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private int karma;
	private String receiver;
	private String giver;
	private String channel;
	private String server;

	public KarmaEntity()
	{
	}
	
	public long getId()
	{
		return id;
	}
	
	public int getKarma()
	{
		return karma;
	}
	
	public void setKarma(int karma)
	{
		this.karma = karma;
	}
	
	public String getReceiver()
	{
		return receiver;
	}
	
	public void setReceiver(String nickname)
	{
		this.receiver = receiver;
	}

	public String getGiver()
	{
		return giver;
	}

	public void setGiver(String giver)
	{
		this.giver = giver;
	}

	public String getChannel()
	{
		return channel;
	}
	
	public void setChannel(String channel)
	{
		this.channel = channel;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public void setServer(String server)
	{
		this.server = server;
	}
}
