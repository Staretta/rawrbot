package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.List;

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

@Entity
@Table(name = "channels")
public class ChannelEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "channels" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String channel;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "channel_modules", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "channel_modules")
	private List<String> modules;
	@ManyToOne
	private Settings settings;

	public ChannelEntity()
	{

	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public List<String> getModules()
	{
		return modules;
	}

	public void setModules(List<String> modules)
	{
		this.modules = modules;
	}
}
