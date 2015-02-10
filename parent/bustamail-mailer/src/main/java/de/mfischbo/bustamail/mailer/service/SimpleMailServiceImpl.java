package de.mfischbo.bustamail.mailer.service;

import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.processor.FilePublisherStep;
import de.mfischbo.bustamail.mailer.processor.HTMLProcessorStep;
import de.mfischbo.bustamail.mailer.processor.IMailingProcessorStep;
import de.mfischbo.bustamail.mailer.processor.JobActivatorStep;
import de.mfischbo.bustamail.mailer.processor.JobFolderProcessingStep;
import de.mfischbo.bustamail.mailer.processor.MailingSerializerProcessorStep;
import de.mfischbo.bustamail.mailer.processor.PreviewMailingProcessorStep;
import de.mfischbo.bustamail.mailer.util.MailingSerializer;

@Service
public class SimpleMailServiceImpl implements SimpleMailService {

	@Inject
	private JavaMailSender mailSender;
	
	@Inject
	private Environment		env;
	
	@Inject
	private MailingSerializer	serializer;

	private Logger log = LoggerFactory.getLogger(getClass());
	
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
	public boolean scheduleLiveMailing(LiveMailing m) throws Exception {
		log.info("Scheduled live mailing {}", m.getSubject());
		
		// process html / text content
		log.debug("Processing HTML / Text content preparations");
		HTMLProcessorStep s1 = new HTMLProcessorStep();
		m = s1.process(m);
		
		// publish all resources in the document root
		log.debug("Publishing resource files");
		FilePublisherStep s2 = new FilePublisherStep(env);
		m = s2.process(m);
		
		// create the folder structure for this mailing
		log.debug("Creating job folder environment for the mailer");
		JobFolderProcessingStep s3 = new JobFolderProcessingStep(env);
		m = s3.process(m);
	
		// create a serialized mailing for each recipient in the job folder
		log.debug("Serializing actual e-mails");
		MailingSerializerProcessorStep s4 = new MailingSerializerProcessorStep(env, serializer);
		m = s4.process(m);
	
		// set the activation flag in the job folder to start the mailing
		log.debug("Activating the job");
		JobActivatorStep s5 = new JobActivatorStep(env);
		m = s5.process(m);
		return true;
	}
	
	@Override
	public void scheduleOptinMailing(LiveMailing m) throws Exception {
		log.debug("Processing HTML / Text content preparations");
		IMailingProcessorStep s1 = new HTMLProcessorStep();
		m = s1.process(m);
		IMailingProcessorStep s2 = new FilePublisherStep(env);
		m = s2.process(m);
		for (PersonalizedEmailRecipient r : m.getRecipients()) {
			try {
				InternetAddress rec = new InternetAddress(r.getEmail());
				this.sendSimpleHtmlMail(m.getSenderAddress(), m.getSenderName(), "", rec, m.getSubject(), m.getHtmlContent());
			} catch (Exception ex) {
				log.error("Unable to send optin mail. Cause: " + ex.getMessage());
			}
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailer.service.SimpleMailService#sendPreviewMailing(de.mfischbo.bustamail.mailer.PreviewMailing)
	 */
	@Override
	public void sendPreviewMailing(LiveMailing mailing) throws BustaMailException {
		log.info("Scheduled preview mailing {}", mailing.getSubject());
	
		log.info("Preparing the mailing to be sent as preview");
		IMailingProcessorStep s1 = new PreviewMailingProcessorStep();
		mailing = s1.process(mailing);
		
		log.debug("Processing HTML / Text content preparations");
		IMailingProcessorStep s2 = new HTMLProcessorStep();
		mailing = s2.process(mailing);
		
		log.debug("Publishing resource files for mailing");
		IMailingProcessorStep s3 = new FilePublisherStep(env);
		mailing = s3.process(mailing);
	
		for (PersonalizedEmailRecipient r : mailing.getRecipients()) {
			try {
				InternetAddress rec = new InternetAddress(r.getEmail());
				this.sendSimpleHtmlMail(mailing.getSenderAddress(), mailing.getSenderName(), "", rec, mailing.getSubject(), mailing.getHtmlContent());
			} catch (Exception ex) {
				log.error("Unable to send preview. Cause: " + ex.getMessage());
			}
		}
	}
}
