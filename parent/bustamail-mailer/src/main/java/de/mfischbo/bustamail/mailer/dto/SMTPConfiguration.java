package de.mfischbo.bustamail.mailer.dto;

import java.util.Properties;

import org.springframework.core.env.Environment;


public class SMTPConfiguration {

	public enum SMTPAuthentication {
		NO_AUTH,
		USERNAME_PASSWORD
	}
	
	private String			hostname;
	private String			username;
	private String			password;
	private String			protocol;
	
	private long			mailSendingDelay;
	private long			maxMailsPerConnection;
	private long			reconnectAfter;
	
	private SMTPAuthentication	authentication;
	
	public SMTPConfiguration() {
		
	}
	
	public SMTPConfiguration(Environment env) {
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public SMTPAuthentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(SMTPAuthentication authentication) {
		this.authentication = authentication;
	}
	
	public Properties asProperties() {
		return null;
	}

	public long getMailSendingDelay() {
		return mailSendingDelay;
	}

	public void setMailSendingDelay(long mailSendingDelay) {
		this.mailSendingDelay = mailSendingDelay;
	}

	public long getMaxMailsPerConnection() {
		return maxMailsPerConnection;
	}

	public void setMaxMailsPerConnection(long maxMailsPerConnection) {
		this.maxMailsPerConnection = maxMailsPerConnection;
	}

	public long getReconnectAfter() {
		return reconnectAfter;
	}

	public void setReconnectAfter(long reconnectAfter) {
		this.reconnectAfter = reconnectAfter;
	}
}
