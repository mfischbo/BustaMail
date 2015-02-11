package de.mfischbo.bustamail.mailer.processor;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.internet.AddressException;

import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.gridfs.GridFSDBFile;

import de.mfischbo.bustamail.common.domain.DefaultTemplateMarkers;
import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.IMailableMail;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.util.HTMLSourceProcessor;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.util.MediaUtils;

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
@Component
public class MailingPreProcessor {

	private static final String 	BASE_API_KEY = "de.mfischbo.bustamail.mailing.baseUrl";
	private static final String		WEBSERVER_KEY= "de.mfischbo.bustamail.env.liveUrl";
	
	@Inject GridFsTemplate gridTemplate;
	
	@Inject Environment		env;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
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
	public LiveMailing createLiveMailing(IMailableMail m, Collection<PersonalizedEmailRecipient> recipients, String html, 
			String text, Map<String, Object> mailingData) throws Exception {
	
		URL u = new URL(env.getProperty(WEBSERVER_KEY) + "/mailing_" + m.getId());
		URL api = new URL(env.getProperty(BASE_API_KEY));
		log.debug("Setting HTTP resource root for this mailing to {}", u.toString());
		
		LiveMailing lm = null;
		if (text != null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html, text, u, api);
		if (text == null)
			lm = new LiveMailing(m.getId(), m.getSubject(), html, u, api);
		
		log.info("Configuring the HTML Parser...");
		lm.setRemoveClasses(DefaultTemplateMarkers.getTemplateClassMarkers());
		lm.setRemoveAttributes(DefaultTemplateMarkers.getTemplateAttributeMarkers());
		lm.setDisableLinkTrackClass(DefaultTemplateMarkers.getDiableLinkTrackClass());
		
		log.info("Adding {} recipients to the mailing", recipients.size());
		lm.setRecipients(recipients);

		// set sender / reply to addresses and names
		log.info("Setting sender address to : " + m.getSender());
		lm.setSenderAddress(m.getSender());
		
		log.info("Setting reply-to address to : " + m.getReplyTo());
		lm.setReplyToAddress(m.getReplyTo());
		
		log.info("Setting sender name to : " + m.getSenderName());
		lm.setSenderName(m.getSenderName());
		
		log.info("Setting custom mailing data on the mailing");
		lm.setMailingData(mailingData);
		
		// gather resources from the mailing
		log.info("Gathering resources to be used in the mailing");
		Document doc = Jsoup.parse(html);
		Map<ObjectId, List<Integer>> resources = HTMLSourceProcessor.getStaticResourceIds(doc);
		Map<ObjectId, ObjectId> resourceMap = new HashMap<>();
		
		for (ObjectId id : resources.keySet()) {
			for (Integer width : resources.get(id)) {
				Media media = null;
				if (width == -1)
					media = getMediaById(id);
				else
					media = getMediaById(id, width);
				
				lm.getResources().put(media.getId(), getContent(media));
				resourceMap.put(id, media.getId());
			}
		}
		lm.setResourceMap(resourceMap);
		return lm;
	}
	
	private Media getMediaById(ObjectId id) {
		GridFSDBFile file = gridTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
		return MediaUtils.convertFile(file);
	}
	
	private Media getMediaById(ObjectId id, Integer preferedSize) {
		int s = MediaUtils.getBestMatchingSize(preferedSize);
		GridFSDBFile f = gridTemplate.findOne(
				Query.query(
					Criteria.where("metadata.parent").is(id)	
					.and("metadata.width").is(s)
				));
		if (f == null) {
			// fallback to default
			return getMediaById(id);
		}
		return MediaUtils.convertFile(f);	
	}
	
	private InputStream getContent(Media media) {
		GridFSDBFile f = gridTemplate.findOne(Query.query(Criteria.where("_id").is(media.getId())));
		if (f != null) {
			return f.getInputStream();
		}
		return null;
	}
}
