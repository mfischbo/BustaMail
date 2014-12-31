package de.mfischbo.bustamail.bouncemail.domain;

import java.util.Properties;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.User;

@Document(collection = "Bounce_BounceAccount")
public class BounceAccount extends OwnedBaseDomain {

	private static final long serialVersionUID = -500515799027899394L;

	public enum AccountType {
		POP3,
		IMAP
	}
	
	public enum SecurityType {
		NONE,
		SSL,
		STARTTLS
	}
	
	private String 		name;
	private String 		description;

	private AccountType accountType;
	
	private String		address;
	
	private String 		hostname;
	private int			port;

	private String		username;
	private String		password;
	
	private int			pollInterval;

	private SecurityType securityType;
	
	private boolean 	enabled;
	private boolean 	removeOnRead;
	
	@DBRef
	private User		userCreated;
	
	@DBRef
	private User		userModified;
	
	private DateTime	dateCreated;
	private DateTime	dateModified;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPollInterval() {
		return pollInterval;
	}

	public void setPollInterval(int pollInterval) {
		this.pollInterval = pollInterval;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public SecurityType getSecurityType() {
		return securityType;
	}

	public void setSecurityType(SecurityType securityType) {
		this.securityType = securityType;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public User getUserModified() {
		return userModified;
	}

	public void setUserModified(User userModified) {
		this.userModified = userModified;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public DateTime getDateModified() {
		return dateModified;
	}

	public void setDateModified(DateTime dateModified) {
		this.dateModified = dateModified;
	}

	public boolean isRemoveOnRead() {
		return removeOnRead;
	}

	public void setRemoveOnRead(boolean removeOnRead) {
		this.removeOnRead = removeOnRead;
	}
	
	@Transient
	@JsonIgnore
	public Properties getConnectionProperties() {
		Properties p = new Properties();
		
		p.put("mail.host", this.hostname);
	
		if (this.accountType == AccountType.POP3) {
			p.put("mail.pop3.auth", "true");
			p.put("mail.pop3.port", this.port);
			p.put("mail.store.protocol", "pop3");
			
			if (this.securityType == SecurityType.STARTTLS) {
				p.put("mail.pop3.starttls.enable", "true");
				p.put("mail.pop3.ssl.trust", this.hostname);
			}
			
			if (this.securityType == SecurityType.SSL) {
				p.put("mail.pop3.ssl.enable", "true");
				p.put("mail.pop3.ssl.trust", this.hostname);
			}
		
		} else if (this.accountType == AccountType.IMAP) {
			p.put("mail.imap.auth", "true");
			p.put("mail.imap.port", this.port);
			p.put("mail.store.protocol", "imap");
		
			if (this.securityType == SecurityType.STARTTLS) {
				p.put("mail.imap.starttls.enable", "true");
				p.put("mail.pop3.ssl.trust", this.hostname);
			}
			
			if (this.securityType == SecurityType.SSL) {
				p.put("mail.pop3.ssl.enable", "true");
				p.put("mail.pop3.ssl.trust", this.hostname);
			}
		}
		return p;
	}
}