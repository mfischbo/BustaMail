package de.mfischbo.bustamail.mailing.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.util.HTMLSourceProcessor;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
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
	public static LiveMailing createLiveMailing(Mailing m, Collection<PersonalizedEmailRecipient> recipients, VersionedContent html, 
			VersionedContent text, String webServerURL, String apiURL, MediaService mService) throws Exception {
	
		Logger log = LoggerFactory.getLogger(MailingPreProcessor.class);
	
		URL u = new URL(webServerURL);
		URL api = new URL(apiURL);
		
		LiveMailing lm = null;
		if (text != null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html.getContent(), text.getContent(), u, api);
		if (text == null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html.getContent(), u, api);
		
		log.info("Configuring the HTML Parser...");
		lm.setRemoveClasses(DefaultTemplateMarkers.getTemplateClassMarkers());
		lm.setRemoveAttributes(DefaultTemplateMarkers.getTemplateAttributeMarkers());
		lm.setDisableLinkTrackClass(DefaultTemplateMarkers.getDiableLinkTrackClass());
		
		log.info("Adding {} recipients to the mailing", recipients.size());
		lm.setRecipients(recipients);

		// set sender / reply to addresses and names
		log.info("Setting sender address to : " + m.getSenderAddress());
		lm.setSenderAddress(new InternetAddress(m.getSenderAddress()));
		
		log.info("Setting reply-to address to : " + m.getReplyAddress());
		lm.setReplyToAddress(new InternetAddress(m.getReplyAddress()));
		
		log.info("Setting sender name to : " + m.getSenderName());
		lm.setSenderName(m.getSenderName());
		
		// gather resources from the mailing
		log.info("Gathering resources to be used in the mailing");
		Document doc = Jsoup.parse(html.getContent());
		Map<ObjectId, List<Integer>> resources = HTMLSourceProcessor.getStaticResourceIds(doc);
		Map<ObjectId, ObjectId> resourceMap = new HashMap<>();
		
		for (ObjectId id : resources.keySet()) {
			for (Integer width : resources.get(id)) {
				Media media = null;
				if (width == -1)
					media = mService.getMediaById(id);
				else
					media = mService.getMediaById(id, width);
				
				lm.getResources().put(media.getId(), mService.getContent(media));
				resourceMap.put(id, media.getId());
			}
		}
		lm.setResourceMap(resourceMap);
		return lm;
	}
}
