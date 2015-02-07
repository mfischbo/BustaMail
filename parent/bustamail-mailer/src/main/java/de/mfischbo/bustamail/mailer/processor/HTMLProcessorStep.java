package de.mfischbo.bustamail.mailer.processor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.util.HTMLSourceProcessor;

public class HTMLProcessorStep implements IMailingProcessorStep {

	Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public LiveMailing process(LiveMailing mailing) throws BustaMailException {
	
		if (mailing.getApiURL() == null)
			throw new IllegalArgumentException("No API URL is configured. Unable to continue content preparation");
		if (mailing.getWebServerBaseURL() == null)
			throw new IllegalArgumentException("No Webserver URL is configured. Unable to continue content preparation");
		
		try {
			String html = "";
			String text = "";
		
			if (mailing.getHtmlContent() != null && mailing.getHtmlContent().trim().length() > 0) 
				html = prepareBaseHTMLContent(mailing);
			if (mailing.getTextContent() != null && mailing.getTextContent().trim().length() > 0)
				text = prepareBaseTextContent(mailing);
			
			mailing.setHtmlContent(html);
			mailing.setTextContent(text);
		} catch (Exception ex) {
			log.error("Failed to prepare HTML/TEXT contents. Cause: "	 + ex.getMessage());
			log.error("Scheduling of live mailing for id : " + mailing.getMailingId() + " failed");
			throw new BustaMailException("Failed on HTML/Text content preparation step.");
		}
		return mailing;
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
			doc = HTMLSourceProcessor.replaceSpanCells(doc, m.getWebServerBaseURL(), "/blank.gif");
		}
	
		log.info("Replacing static link if present");
		doc = HTMLSourceProcessor.replaceStaticLink(doc, m.getWebServerBaseURL(), m.getDisableLinkTrackClass());
		
		if (m.isEnableOpeningTracking()) {
			log.info("Attaching opening tracking pixel...");
			doc = HTMLSourceProcessor.attachOpeningPixel(doc, m.getApiURL());
		}
		
		if (m.isEnableLinkTracking()) {
			log.info("Replacing link tracking urls...");
			doc = HTMLSourceProcessor.createTrackingUrls(doc, m.getApiURL(), m.getDisableLinkTrackClass());
		}
		
		log.info("Replacing source URLs");
		doc = HTMLSourceProcessor.replaceSourceURLs(doc, m.getWebServerBaseURL(), m.getResourceMap(), m.getDisableLinkTrackClass());
	
	
		log.info("Removing Editor class/attribute markers");
		doc = HTMLSourceProcessor.removeAttributes(doc, m.getRemoveAttributes());
		doc = HTMLSourceProcessor.removeClasses(doc, m.getRemoveClasses());
		
		log.info("Cleaning up the document");
		doc = HTMLSourceProcessor.cleanUp(doc);
		return doc.html();
	}
}