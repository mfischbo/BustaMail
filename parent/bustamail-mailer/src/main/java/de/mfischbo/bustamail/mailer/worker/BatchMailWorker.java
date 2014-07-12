package de.mfischbo.bustamail.mailer.worker;

import java.io.File;
import java.io.FilenameFilter;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.smtp.SMTPTransport;

import de.mfischbo.bustamail.mailer.dto.SMTPConfiguration;
import de.mfischbo.bustamail.mailer.dto.SMTPConfiguration.SMTPAuthentication;
import de.mfischbo.bustamail.mailer.dto.SerializedMailing;

public class BatchMailWorker {

	private Logger log = LoggerFactory.getLogger(BatchMailWorker.class);
	
	private ObjectMapper mapper;
	
	private InternetAddress senderAddress = null;
	
	private Session			session;
	
	private SMTPTransport	transport;
	
	private File			jobFolder;
	
	private SMTPConfiguration	config;
	
	public BatchMailWorker(File folder, ObjectMapper mapper, SMTPConfiguration fallbackConfig) {
		this.mapper = mapper;
		this.jobFolder = folder;
	
		// Read the configuration file for the SMTP connection or fallback to default
		log.info("Reading configuration file if present");
		File configFile = new File(folder.getAbsolutePath() + "/.config");
		try {
			if (configFile.exists()) {
				this.config = mapper.readValue(configFile, SMTPConfiguration.class);
			} else {
				log.info("No .config file present in " + folder.getAbsolutePath() + ". Using default SMTP configuration");
				this.config = fallbackConfig;
			}
		} catch (Exception ex) {
			log.error("Reading .config file failed. Cause: " + ex.getMessage());
		}
		
	
		// create a new connection to the SMTP server
		log.info("Creating connection to the server at: " + this.config.getHostname());
		try {
			createMailSessionFromConfiguration();
		} catch (Exception ex) {
			log.error("Unable to connect to the configured SMTP server. Cause: " + ex.getMessage());
		}
	}

	/**
	 * Executes the job on the provided job folder
	 */
	@Async
	public void execute() {
		log.info("Starting mailings on folder : " + jobFolder.getAbsolutePath());
		try {
			File processingFile = new File(jobFolder.getAbsolutePath() + "/.running");
			processingFile.createNewFile();
			log.info("Setting state for job folder to processing");
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		// load all files from the jobs directory
		File[] mailings = jobFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		});
		log.info("Found " + mailings.length + " Mailings to be sent.");
		
		// process and send each mailing
		for (File f : mailings) {
			try {
				SerializedMailing m = mapper.readValue(f, SerializedMailing.class);
				sendMessage(m);
			} catch (Exception ex) {
				log.error("Unable to deserialize json from file : " + f.getAbsolutePath());
			}
		}
	}
	
	
	private void sendMessage(final SerializedMailing m) throws Exception {
		
		MimeMessage message = new MimeMessage(this.session);
		if (senderAddress != null || !m.getSenderAddress().equals(senderAddress.toString())) {
			try {
				senderAddress = new InternetAddress(m.getSenderAddress());
			} catch (Exception ex) { }
		}
		
		message.addRecipient(RecipientType.TO, new InternetAddress(m.getRecipientAddress()));
		message.setSender(senderAddress);
		message.setFrom(m.getSenderName() + "<" + m.getSenderAddress() + ">");
	
		if (!m.getReplyToAddress().isEmpty()) {
			try {
				InternetAddress[] replies = new InternetAddress[0];
				replies[0] = new InternetAddress(m.getReplyToAddress());
				message.setReplyTo(replies);
			} catch (Exception ex) {
				
			}
		}
		message.setSubject(m.getSubject());
		message.setHeader("Content-Type", "text/html;charset=utf-8");
		
		MimeMultipart content = new MimeMultipart("alternative");
		MimeBodyPart htmlBody = new MimeBodyPart();
		htmlBody.setText(m.getHtmlContent());
		htmlBody.setHeader("Content-Type", "text/html;charset=UTF-8");
		htmlBody.setHeader("Content-Transfer-Encoding", "quoted-printable");
		content.addBodyPart(htmlBody);
		
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setText(m.getTextContent());
		textBody.setHeader("Content-Type", "text/plain;charset=UTF-8");
		content.addBodyPart(textBody);
		message.setContent(content);
		message.saveChanges();
		this.transport.sendMessage(message, message.getAllRecipients());
	}
	
	
	private void createMailSessionFromConfiguration() throws MessagingException {
		if (this.config.getAuthentication().equals(SMTPAuthentication.USERNAME_PASSWORD)) {
			this.session = Session.getInstance(config.asProperties(), new Authenticator() {
			
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(config.getUsername(), config.getPassword());
				}
			});
		} else
			this.session = Session.getInstance(config.asProperties());
		this.transport = (SMTPTransport) session.getTransport(config.getProtocol());
		this.transport.connect(config.getHostname(), config.getUsername(), config.getPassword());
	}
}
