package de.mfischbo.bustamail.mailing.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.subscriber.dto.RecipientDTO;
import de.mfischbo.bustamail.template.util.DefaultTemplateMarkers;
import de.mfischbo.bustamail.vc.domain.VersionedContent;

/**
 * This class provides pre processing capabilities for a mailing.
 * 
 * The {@link MailingPreProcessor#createLiveMailing(Mailing, Collection, VersionedContent, VersionedContent, String)} creates
 * a new LiveMailing and ensures that all parameters are set correctly in order to successfully send the mailing.
 * The resulting object contains the template for the mailing as well as all subscribers the mailing will be sent to.
 * 
 * @author M. Fischboeck
 *
 */
public class MailingPreProcessor {

	/**
	 * Creates a {@link LiveMailing} from the given parameters. 
	 * @param m The Mailing to be processed
	 * @param subscriptions The list of subscriptions for this mailing
	 * @param html The HTML content for this mailing
	 * @param text The text content for this mailing
	 * @param contentBaseUrl the base URL for static resources to be used
	 * @return The prepared live mailing
	 * @throws AddressException
	 * @throws MalformedURLException
	 */
	public static LiveMailing createLiveMailing(Mailing m, Collection<Subscription> subscriptions, VersionedContent html, 
			VersionedContent text, String contentBaseUrl) throws AddressException, MalformedURLException {
	
		Logger log = LoggerFactory.getLogger(MailingPreProcessor.class);
	
		URL u = new URL(contentBaseUrl);
		
		LiveMailing lm = null;
		if (text != null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html.getContent(), text.getContent(), u);
		if (text == null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html.getContent(), u);
		
		// collect all recipients
		Set<PersonalizedEmailRecipient> recipients = new HashSet<>();
		for (Subscription s : subscriptions) {
			
			if (s.getState() != State.ACTIVE)
				continue;
			
			RecipientDTO d = new RecipientDTO();
			d.setAddress(s.getEmailAddress());
			d.setFirstName(s.getContact().getFirstName());
			d.setLastName(s.getContact().getLastName());
			d.setFormalSalutation(s.getContact().isFormalSalutation());
			d.setGender(s.getContact().getGender());
			d.setId(s.getId());
			recipients.add(d);
		}
		
		lm.setRecipients(recipients);
		log.info("Added " + recipients.size() + " subscribers to this mailing");
		
		log.info("Configuring the HTML Parser...");
		lm.setRemoveClasses(DefaultTemplateMarkers.getTemplateClassMarkers());
		lm.setRemoveAttributes(DefaultTemplateMarkers.getTemplateAttributeMarkers());
		lm.setDisableLinkTrackClass(DefaultTemplateMarkers.getDiableLinkTrackClass());

		// check for cell padding optimization
		/*
		Template t = m.getTemplate();
		if (t.getSettings().containsKey(Template.SKEY_CELLPADDING_OPTIMIZATION)) {
			log.info("Enabling cell padding optimizations");
			lm.setSpanCellReplacement(Boolean.parseBoolean(t.getSettings().get(Template.SKEY_CELLPADDING_OPTIMIZATION)));
		}
		*/
		
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
