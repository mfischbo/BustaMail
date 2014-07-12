package de.mfischbo.bustamail.mailing.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.domain.VersionedContent;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.subscriber.dto.RecipientDTO;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.util.DefaultTemplateMarkers;

public class MailingPreProcessor {

	public static LiveMailing createLiveMailing(Mailing m, VersionedContent html, VersionedContent text, String contentBaseUrl) throws AddressException, MalformedURLException {
	
		Logger log = LoggerFactory.getLogger(MailingPreProcessor.class);
	
		URL u = new URL(contentBaseUrl);
		
		LiveMailing lm = null;
		if (text != null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html.getContent(), text.getContent(), u);
		if (text == null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html.getContent(), u);
		
		// collect all recipients
		Set<PersonalizedEmailRecipient> recipients = new HashSet<>();
		for (SubscriptionList l : m.getSubscriptionLists()) {
			
			log.info("Adding subscribers from subscription list : " + l.getName());
			for (Subscription s : l.getSubscriptions()) {
				RecipientDTO d = new RecipientDTO();
				d.setAddress(s.getEmailAddress());
				d.setFirstName(s.getEmailAddress().getContact().getFirstName());
				d.setLastName(s.getEmailAddress().getContact().getLastName());
				d.setFormalSalutation(s.getEmailAddress().getContact().isFormalSalutation());
				d.setGender(s.getEmailAddress().getContact().getGender());
				d.setSubscriberId(s.getId());
				recipients.add(d);
			}
		}
		
		lm.setRecipients(recipients);
		log.info("Added " + recipients.size() + " subscribers to this mailing");
		
		log.info("Configuring the HTML Parser...");
		lm.setRemoveClasses(DefaultTemplateMarkers.getTemplateClassMarkers());
		lm.setRemoveAttributes(DefaultTemplateMarkers.getTemplateAttributeMarkers());
		lm.setDisableLinkTrackClass(DefaultTemplateMarkers.getDiableLinkTrackClass());

		// check for cell padding optimization
		Template t = m.getTemplate();
		if (t.getSettings().containsKey(Template.SKEY_CELLPADDING_OPTIMIZATION)) {
			log.info("Enabling cell padding optimizations");
			lm.setSpanCellReplacement(Boolean.parseBoolean(t.getSettings().get(Template.SKEY_CELLPADDING_OPTIMIZATION)));
		}
		
		// set sender / reply to addresses and names
		log.info("Setting sender address to : " + m.getSenderAddress());
		lm.setSenderAddress(new InternetAddress(m.getSenderAddress()));
		
		log.info("Setting reply-to address to : " + m.getReplyAddress());
		lm.setReplyToAddress(new InternetAddress(m.getReplyAddress()));
		
		log.info("Setting sender name to : " + m.getSenderName());
		lm.setSenderName(m.getSenderName());
	
		return lm;
	}
}
