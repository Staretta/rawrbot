package net.staretta.businesslogic.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "eightball_answers")
public class EightballAnswers implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "eightball_answers" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long				id;
	private String				message;

	public EightballAnswers()
	{

	}

	public String getMessage()
	{
		return message;
	}
}