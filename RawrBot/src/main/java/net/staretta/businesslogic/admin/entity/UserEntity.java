package net.staretta.businesslogic.admin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.staretta.businesslogic.util.MiscUtil;

@Entity
@Table(name = "admin_user")
// @TypeDef(name = "encryptedString", typeClass = EncryptedStringType.class, parameters = { @Parameter(name = "encryptorRegisteredName",
// value = "strongHibernateStringEncryptor") })
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
	// THE REASON YOU HAD THIS IN PLACE WAS TO TEST THE CHANNEL PASSWORD ENCRYPTION, WHICH NEEDED TO BE TWO WAY ENCRYPTED/DECRYPTED
	// @Type(type = "encryptedString")
	private String password;
	// Email verification
	private boolean verified = false;
	private String verificationCode;
	// Nickserv Identified
	private boolean identified = false;
	private Date registerDate;
	private Date lastLogin;
	// Used to see how long they've been idle, and if they need to re sign in.
	private Date lastActive;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "admin_user_aliases", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "admin_user_aliases")
	private Set<String> aliases = new HashSet<String>();
	
	public UserEntity()
	{
		this.registerDate = new Date();
		this.verificationCode = generateVerificationCode();
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getNickname()
	{
		return nickname;
	}
	
	public String getHostmask()
	{
		return hostmask;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public boolean isVerified()
	{
		return verified;
	}
	
	public boolean isIdentified()
	{
		return identified;
	}
	
	public Date getRegisterDate()
	{
		return registerDate;
	}
	
	public Date getLastLogin()
	{
		return lastLogin;
	}
	
	public Date getLastActive()
	{
		return lastActive;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	
	public void setHostmask(String hostmask)
	{
		this.hostmask = hostmask;
	}
	
	public void setServer(String server)
	{
		this.server = server;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setVerified(boolean verified)
	{
		this.verified = verified;
	}
	
	public void setIdentified(boolean identified)
	{
		this.identified = identified;
	}
	
	public void setRegisterDate(Date registerDate)
	{
		this.registerDate = registerDate;
	}
	
	public void setLastLogin(Date lastLogin)
	{
		this.lastLogin = lastLogin;
	}
	
	public void setLastActive(Date lastActive)
	{
		this.lastActive = lastActive;
	}
	
	public Set<String> getAliases()
	{
		return aliases;
	}
	
	public void addAlias(String alias)
	{
		aliases.add(alias);
	}
	
	public void removeAlias(String alias)
	{
		aliases.remove(alias);
	}
	
	public String getVerificationCode()
	{
		return verificationCode;
	}
	
	public void setVerificationCode(String verificationCode)
	{
		this.verificationCode = verificationCode;
	}
	
	public String generateVerificationCode()
	{
		return MiscUtil.generateRandomString(20, 20);
	}
}
