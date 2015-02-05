package de.mfischbo.bustamail.mailer.service;

import javax.mail.internet.InternetAddress;

import de.mfischbo.bustamail.mailer.dto.LiveMailing;

public interface SimpleMailService {

	/**
	 * Sends a simple text email to the given recipient
	 * @param from The from address
	 * @param to The recipients address
	 * @param subject The subject of the mail
	 * @param text The text content of the mail
	 */
	public void sendSimpleMail(InternetAddress from, InternetAddress to, String subject, String text);
		

	/**
	 * Sends a simple html email (no text alternative) to the given recipient
	 * @param from The from address
	 * @param to The recipients address
	 * @param subject The subject of the mail
	 * @param html The html content of the mail
	 */
	public void sendSimpleHtmlMail(InternetAddress from, String senderName, String replyToAddress, InternetAddress to, String subject, String html);
	
	
	public void sendPreviewMailing(LiveMailing mailing) throws Exception;
	

	/**
	 * Schedules the given mailing to be sent by the batch mailing subsystem.
	 * @param m The LiveMailing that should be scheduled for publishing
	 * @return True, if the mailing could be scheduled successfully, false otherwise
	 */
	public boolean scheduleLiveMailing(LiveMailing m) throws Exception;
}
