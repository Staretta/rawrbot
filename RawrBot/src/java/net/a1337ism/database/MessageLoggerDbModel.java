package net.a1337ism.database;

import java.util.Date;

public class MessageLoggerDbModel
{
	private int		id;
	private String	nickname;
	private String	username;
	private String	hostmask;
	private String	message;
	private String	channel;
	private Date	time;
	private String	type;

	public MessageLoggerDbModel()
	{
	}

	public MessageLoggerDbModel(String nickname, String username, String hostname, String hostmask, String message,
			int time)
	{
	}

	public MessageLoggerDbModel(String nickname, String username, String hostname, String hostmask, String message,
			String channel, int time)
	{
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
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

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date date)
	{
		this.time = date;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}