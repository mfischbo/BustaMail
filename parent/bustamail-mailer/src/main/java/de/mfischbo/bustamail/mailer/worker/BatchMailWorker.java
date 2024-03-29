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

import org.joda.time.DateTime;
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
	
	private InternetAddress senderAddress;
	
	private Session			session;
	
	private SMTPTransport	transport;
	
	private File			jobFolder;
	
	private SMTPConfiguration	config;
	
	public BatchMailWorker(File folder, ObjectMapper mapper, SMTPConfiguration fallbackConfig) {
		this.mapper = mapper;
		this.jobFolder = folder;
		try {
			this.senderAddress = new InternetAddress("test@example.com");
		} catch (Exception ex) { }
	
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
		long i=0;
		long mailsOnSession = 0;
		for (File f : mailings) {
			
			/**
			 * Read the serialized mailing and move it to .failed on failures
			 */
			SerializedMailing m = null;
			try {
				m = mapper.readValue(f, SerializedMailing.class);
			} catch (Exception ex) {
				log.error("Unable to deserialize json from file : " + f.getAbsolutePath());
				
				// move the file to the failed folder, since we are not able to read it
				File dst = new File(jobFolder.getAbsolutePath() + "/.failed/" + f.getName());
				f.renameTo(dst);
				dst.setLastModified(DateTime.now().getMillis());
				continue;
			}
		
			
			/**
			 * Send the mailing. On success move it to .success on failure move it to .retry
			 */
			try {
				sendMessage(m);
				mailsOnSession++;
				// all went well. Move the file to the success folder
				File dst = new File(jobFolder.getAbsolutePath() + "/.success/" + f.getName());
				f.renameTo(dst);
				dst.setLastModified(DateTime.now().getMillis());
				
				log.debug("Sent message\t {} / {} to recipient: {}", ++i, mailings.length, m.getRecipientAddress());
			} catch (Exception ex) {
				log.error("Unable to send message. Cause: " + ex.getMessage());
				File dst = new File(jobFolder.getAbsolutePath() + "/.retry/" + f.getName());
				f.renameTo(dst);
				dst.setLastModified(DateTime.now().getMillis());
			}
		
			
			/**
			 * Check for delay after each sent mail
			 */
			if (config.getMailSendingDelay() != -1) {
				try { Thread.sleep(config.getMailSendingDelay()); } catch (Exception ex) { }
			}
		
			/**
			 * Check for amount of mails in current session
			 */
			if (mailsOnSession >= config.getMaxMailsPerConnection()) {
				mailsOnSession = 0;
				log.info("Session limit ({}) reached. Resetting connection.", config.getMaxMailsPerConnection());
				try {
					transport.close();
				} catch (Exception ex) {
					log.error("Unable to close transport. Cause : " + ex.getMessage());
				}
				
				if (config.getReconnectAfter() > -1) {
					log.info("Delaying reconnection for {} milliseconds.", config.getReconnectAfter());
					try { Thread.sleep(config.getReconnectAfter()); } catch (Exception ex) { }
				}
				
				try {
					log.info("Trying to reconnect.");
					createMailSessionFromConfiguration();
				} catch (Exception ex) {
					log.error("Unable to reconnect to server. Cause: " + ex.getMessage());
				}
			}
		}
	}
	
	/**
	 * Constructs a mime message from the serialized message and sends it using the mailers transport
	 * @param m The SerializedMailing to be sent
	 * @throws Exception On any error when creating the message or sending it
	 */
	private void sendMessage(final SerializedMailing m) throws Exception {
		
		MimeMessage message = new MimeMessage(this.session);
		
		if (senderAddress != null) {
			if (!m.getSenderAddress().equals(senderAddress.toString())) {
				try {
					senderAddress = new InternetAddress(m.getSenderAddress());
				} catch (Exception ex) { }
			}
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
		
		/*
		message.setHeader("Content-Type", "text/html;charset=utf-8");
		message.setHeader("Content-Transfer-Encoding", "quoted-printable");
		message.setText(m.getHtmlContent());
		*/
		
		MimeMultipart content = new MimeMultipart("alternative");
	
		if (m.getTextContent() != null && m.getTextContent().trim().length() > 0) {
			MimeBodyPart textBody = new MimeBodyPart();
			textBody.setText(m.getTextContent());
			textBody.setHeader("Content-Type", "text/plain;charset=UTF-8");
			content.addBodyPart(textBody);
		}
		
		MimeBodyPart htmlBody = new MimeBodyPart();
		htmlBody.setText(m.getHtmlContent());
		htmlBody.setHeader("Content-Type", "text/html;charset=UTF-8");
		htmlBody.setHeader("Content-Transfer-Encoding", "quoted-printable");
		content.addBodyPart(htmlBody);
		
		message.setContent(content);
		message.saveChanges();
		this.transport.sendMessage(message, message.getAllRecipients());
	}
	

	/**
	 * Opens a connection to the configured transport
	 * @throws MessagingException
	 */
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
