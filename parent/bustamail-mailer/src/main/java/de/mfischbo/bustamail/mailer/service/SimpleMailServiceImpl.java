package de.mfischbo.bustamail.mailer.service;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.PreviewMailing;
import de.mfischbo.bustamail.mailer.util.HTMLSourceProcessor;

@Service
public class SimpleMailServiceImpl implements SimpleMailService {

	@Autowired
	private JavaMailSender mailSender;

	private Logger log = Logger.getLogger(getClass());
	
	@Override
	public void sendSimpleMail(InternetAddress from, InternetAddress to, String subject, String text) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(from.getAddress());
		msg.setTo(to.getAddress());
		msg.setSubject(subject);
		msg.setText(text);
		mailSender.send(msg);
	}

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
				m.setHeader("Content-Type", "text/html);charset-utf-8");
				
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
