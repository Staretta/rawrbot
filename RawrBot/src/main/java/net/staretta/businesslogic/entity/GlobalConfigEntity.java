package net.staretta.businesslogic.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "global_config")
public class GlobalConfigEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "global_config" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "config")
	private List<ServerEntity> servers;
	private String youtubeApiKey = "";
	
	public GlobalConfigEntity()
	{
	}
	
	public String getYoutubeApiKey()
	{
		return youtubeApiKey;
	}
	
	public void setYoutubeApiKey(String youtubeApiKey)
	{
		this.youtubeApiKey = youtubeApiKey;
	}
}
