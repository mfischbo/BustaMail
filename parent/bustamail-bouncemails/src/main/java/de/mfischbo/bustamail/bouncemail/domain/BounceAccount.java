package de.mfischbo.bustamail.bouncemail.domain;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.User;

@Document(collection = "Bounce_BounceAccount")
public class BounceAccount extends OwnedBaseDomain {

	private static final long serialVersionUID = -500515799027899394L;

	public enum AccountType {
		POP3,
		IMAP
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

	private boolean 	enabled;
	private boolean		useSSL;
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

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
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
}