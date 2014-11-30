package net.staretta.businesslogic.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "sexdice")
public class SexDiceEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "sexdice" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	
	public static enum DiceType
	{
		ACTION, BODYPART, LOCATION
	}
	
	@Enumerated(EnumType.STRING)
	private DiceType diceType;
	private String message;
	
	public SexDiceEntity()
	{
		
	}
	
	public SexDiceEntity(DiceType diceType, String message)
	{
		this.diceType = diceType;
		this.message = message;
	}
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
}