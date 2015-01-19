package net.staretta.businesslogic.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "admin_user")
public class UserEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "generatorMySeq", sequenceName = "admin_user" + "_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatorMySeq")
	private long id;
	private String username;
	private String nickname;
	private String hostmask;
	private String server;
	private String password;
	private String salt;
	// Email verification
	private boolean verified = false;
	// Nickserv Identified
	private boolean identified = false;
	private Date registerDate;
	private Date lastLogin;
	// Used to see how long they've been idle.
	private Date lastActive;
}
