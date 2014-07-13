package de.mfischbo.bustamail.mailer.dto;

import java.util.Properties;

import org.springframework.core.env.Environment;


public class SMTPConfiguration {

	private static final String KEY_HOST = 		"de.mfischbo.bustamail.smtp.host";
	private static final String KEY_USERNAME = 	"de.mfischbo.bustamail.smtp.username";
	private static final String KEY_PASSWORD =	"de.mfischbo.bustamail.smtp.password";
	private static final String KEY_PORT	=	"de.mfischbo.bustamail.smtp.port";
	private static final String KEY_PROTOCOL = 	"de.mfischbo.bustamail.smtp.protocol";
	private static final String KEY_AUTH	=	"de.mfischbo.bustamail.smtp.auth";
	private static final String KEY_DELAY	=	"de.mfischbo.bustamail.batchmailer.afterSendDelay";
	private static final String KEY_MAX_MAILS =	"de.mfischbo.bustamail.batchmailer.maxMailsPerConnection";
	private static final String KEY_RECONNECT = "de.mfischbo.bustamail.batchmailer.reconnectDelay";
	
	
	public enum SMTPAuthentication {
		NO_AUTH,
		USERNAME_PASSWORD
	}
	
	private String			hostname;
	private String			username;
	private String			password;
	private int				port;
	private String			protocol;
	
	private long			mailSendingDelay;
	private long			maxMailsPerConnection;
	private long			reconnectAfter;
	
	private SMTPAuthentication	authentication;
	
	public SMTPConfiguration() {
		
	}
	
	public SMTPConfiguration(Environment env) {
		this.hostname = env.getProperty(KEY_HOST);
		this.username = env.getProperty(KEY_USERNAME);
		this.password = env.getProperty(KEY_PASSWORD);
		this.port	  = Integer.parseInt(env.getProperty(KEY_PORT));
		this.protocol = env.getProperty(KEY_PROTOCOL);
		
		this.mailSendingDelay = Long.parseLong(env.getProperty(KEY_DELAY));
		this.maxMailsPerConnection = Long.parseLong(env.getProperty(KEY_MAX_MAILS));
		this.reconnectAfter = Long.parseLong(env.getProperty(KEY_RECONNECT));
		
		// auth mechanism
		this.authentication = SMTPAuthentication.NO_AUTH;
		String auth = env.getProperty(KEY_AUTH);
		if (auth.equals("USERNAME_PASSWORD"))
			this.authentication = SMTPAuthentication.USERNAME_PASSWORD;
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
		Properties p = new Properties();
		p.setProperty("mail.transport.protocol", this.protocol);
		p.setProperty("mail.host", this.hostname);
		p.setProperty("mail.user", this.username);
		p.setProperty("mail.password", this.password);
		
		if (this.authentication != SMTPAuthentication.NO_AUTH) {
			p.setProperty("mail.smtps.auth", "true");
			p.setProperty("mail.smtp.auth", "true");
			p.setProperty("mail.auth", "true");
		}
		return p;
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
