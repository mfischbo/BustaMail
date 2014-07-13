package de.mfischbo.bustamail.mailer.service;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.dto.PreviewMailing;
import de.mfischbo.bustamail.mailer.util.HTMLSourceProcessor;
import de.mfischbo.bustamail.mailer.util.MailingSerializer;

@Service
public class SimpleMailServiceImpl implements SimpleMailService {

	public static final String		CNF_JOBDIR_KEY = "de.mfischbo.bustamail.mailer.batch.basedir";
	private static final String		CNF_S_FAIL_KEY = "de.mfischbo.bustamail.mailer.batch.failOnSerializationFailure";
	
	@Inject
	private JavaMailSender mailSender;
	
	@Inject
	private Environment		env;
	
	@Inject
	private MailingSerializer	serializer;

	private Logger log = Logger.getLogger(getClass());
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailer.service.SimpleMailService#sendSimpleMail(javax.mail.internet.InternetAddress, javax.mail.internet.InternetAddress, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendSimpleMail(InternetAddress from, InternetAddress to, String subject, String text) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(from.getAddress());
		msg.setTo(to.getAddress());
		msg.setSubject(subject);
		msg.setText(text);
		mailSender.send(msg);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailer.service.SimpleMailService#sendSimpleHtmlMail(javax.mail.internet.InternetAddress, java.lang.String, java.lang.String, javax.mail.internet.InternetAddress, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendSimpleHtmlMail(final InternetAddress from, String senderName, String replyToAddress, final InternetAddress to,
			final String subject, final String html) {
	
		MimeMessagePreparator p = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage m) throws Exception {
				m.setRecipient(RecipientType.TO, to);
				m.setSender(from);
				m.setFrom(senderName + "<" + from.getAddress() + ">");
				
				if (!replyToAddress.isEmpty()) {
					try {
						InternetAddress[] replies = new InternetAddress[0];
						replies[0] = new InternetAddress(replyToAddress);
						m.setReplyTo(replies);
					} catch (Exception ex) {
						
					}
				}
				
				m.setSubject(subject);
				m.setHeader("Content-Type", "text/html;charset-utf-8");
				
				MimeMultipart content = new MimeMultipart("alternative");
				MimeBodyPart htmlBody = new MimeBodyPart();
				htmlBody.setText(html);
				htmlBody.setHeader("Content-Type", "text/html;charset=UTF-8");
				htmlBody.setHeader("Content-Transfer-Encoding", "quoted-printable");
				content.addBodyPart(htmlBody);
				m.setContent(content);
			}
		};
		
		try {
			this.mailSender.send(p);
		} catch (Exception ex) {
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailer.service.SimpleMailService#scheduleLiveMailing(de.mfischbo.bustamail.mailer.LiveMailing)
	 */
	@Override
	public boolean scheduleLiveMailing(LiveMailing m) {
		assert(m.getHtmlContent() != null || m.getTextContent() != null);
		assert(m.getSenderAddress() != null);
		assert(m.getReplyToAddress() != null);
		
		// preprocess the HTML content of the mailing
		String html = "";
		String text = "";
		try {
			if (m.getHtmlContent() != null && m.getHtmlContent().trim().length() > 0) 
				html = prepareBaseHTMLContent(m);
			if (m.getTextContent() != null && m.getTextContent().trim().length() > 0)
				text = prepareBaseTextContent(m);
		} catch (Exception ex) {
			log.error("Failed to prepare HTML/TEXT contents. Cause: " + ex.getMessage());
			log.error("Scheduling of live mailing for id : " + m.getMailingId() + " failed");
			return false;
		}
		
		
		// create the folder structure for this mailing
		File jobFolder;
		try {
			log.info("Creating job folder for this mailing");
			jobFolder = new File(env.getProperty(CNF_JOBDIR_KEY) + "/" + m.getMailingId());
			if (jobFolder.exists()) {
				log.error("Found a job folder with the same matching path at : " + jobFolder.getAbsolutePath());
				log.error("This indicates that the publishing has been already triggered. Aborting this attempt!");
				throw new RuntimeException("Found a job folder with the same path at : " + jobFolder.getAbsolutePath());
			} else {
				jobFolder.mkdirs();
			}
			
			log.info("Creating subfolders for success/failed/retry mailings");
			boolean sub = true;
			File success = new File(jobFolder.getAbsolutePath() + "/success");
			File failed  = new File(jobFolder.getAbsolutePath() + "/failed");
			File retry   = new File(jobFolder.getAbsolutePath() + "/retry");
			sub &= success.mkdir();
			sub &= failed.mkdir();
			sub &= retry.mkdir();
			
			if (!sub) {
				log.error("Failed to create success/failed/retry folders in job folder");
				jobFolder.delete();
				return false;
			}
			
		} catch (Exception ex) {
			log.error("Failed to create job output folder for mailing " + m.getMailingId());
			log.error("Cause: " + ex.getMessage());
			return false;
		}
	
		// create a serialized mailing for each recipient in the job folder
		boolean failFast = Boolean.parseBoolean(env.getProperty(CNF_S_FAIL_KEY));
		
		try {
			for (PersonalizedEmailRecipient r : m.getRecipients()) {
				boolean success = serializer.serializeMailing(jobFolder, m, html, text, r);
				if (!success) {
					log.warn("Failed to serialize mailing for recipient: " + r.getEmail());
					if (failFast) {
						log.error("Aborting scheduling of mailing due to previous warning since fail fast is set to true");
						jobFolder.delete();
						return false;
					}
				}
			}
		} catch (Exception ex) {
			log.error("Problem occured during mailing serialization. Cause: " + ex.getMessage());
			jobFolder.delete();
			return false;
		}
		
		// set the activation flag in the job folder to start the mailing
		File activation = new File(jobFolder.getAbsolutePath() + "/.activate");
		if (!activation.exists()) {
			try {
				activation.createNewFile();
				log.info("Created activation file to trigger the mailer to start mailing");
			} catch (IOException ex) {
				jobFolder.delete();
				return false;
			}
		}
		return true;
	}

	private String prepareBaseTextContent(LiveMailing m) {
		// TODO Implement this
		return m.getTextContent();
	}
	
	private String prepareBaseHTMLContent(LiveMailing m) {
		String html = m.getHtmlContent();
		assert(html != null && html.trim().length() > 0);
		
		Document doc = Jsoup.parse(html);
		
		if (m.isSpanCellReplacement()) {
			log.info("Running span cell optimization...");
			doc = HTMLSourceProcessor.replaceSpanCells(doc, m.getContentProviderBaseURL(), "/img/blank.gif");
		}
		
		log.info("Replacing source URLs");
		doc = HTMLSourceProcessor.replaceSourceURLs(doc, m.getContentProviderBaseURL(), m.getDisableLinkTrackClass());
		
		log.info("Removing Editor class/attribute markers");
		doc = HTMLSourceProcessor.removeAttributes(doc, m.getRemoveAttributes());
		doc = HTMLSourceProcessor.removeClasses(doc, m.getRemoveClasses());
		
		log.info("Cleaning up the document");
		doc = HTMLSourceProcessor.cleanUp(doc);
		return doc.html();
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailer.service.SimpleMailService#sendPreviewMailing(de.mfischbo.bustamail.mailer.PreviewMailing)
	 */
	@Override
	public void sendPreviewMailing(PreviewMailing mailing) {

		assert(mailing.getHtmlContent() != null || mailing.getTextContent() != null);
		
		// preproceses the html content of the mailing
		String html = null;
		if (mailing.getHtmlContent() != null) {
			Document doc = Jsoup.parse(mailing.getHtmlContent());

			// replace all span cells with blank pixel
			if (mailing.isSpanCellReplacement())
				doc = HTMLSourceProcessor.replaceSpanCells(doc, mailing.getContentProviderBaseURL(), "/img/blank.gif");
			
			// replaces image urls and appends tracking links
			doc = HTMLSourceProcessor.replaceSourceURLs(doc, mailing.getContentProviderBaseURL(), mailing.getDisableLinkTrackClass());
		
			// removes all template class and attribute markers
			doc = HTMLSourceProcessor.removeClasses(doc, mailing.getRemoveClasses());
			doc = HTMLSourceProcessor.removeAttributes(doc, mailing.getRemoveAttributes());
			
			// final cleaning stage
			doc = HTMLSourceProcessor.cleanUp(doc);

			html = doc.html();
			log.debug(html);
		}

		for (PersonalizedEmailRecipient r : mailing.getRecipients()) {
			try {
				InternetAddress rec = new InternetAddress(r.getEmail());
				this.sendSimpleHtmlMail(mailing.getSenderAddress(), mailing.getSenderName(), "", rec, mailing.getSubject(), html);
			} catch (Exception ex) {
				log.error("Unable to send preview. Cause: " + ex.getMessage());
			}
		}
	}
}
