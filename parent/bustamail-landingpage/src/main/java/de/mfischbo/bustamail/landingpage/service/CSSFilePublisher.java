package de.mfischbo.bustamail.landingpage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

class CSSFilePublisher {
	
	private static final String CSS_CONCATENATION_KEY = "de.mfischbo.bustamail.langingpages.publisher.enableCSSConcatenation";
	
	private File 				cssResources;
	private MediaService		mService;
	private List<Media>			images;
	private LandingPage			page;
	
	private Map<ObjectId, String>	cssSources;
	private List<String>		cssLinks;
	private boolean				concatenationEnabled;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public CSSFilePublisher(Environment env, File cssResources, MediaService mService, LandingPage page) {
		this.cssResources = cssResources;
		this.mService = mService;
		this.images = new LinkedList<>();
		this.cssSources = new HashMap<>();
		this.cssLinks = new LinkedList<>();
		this.page = page;
		this.concatenationEnabled = Boolean.parseBoolean(env.getProperty(CSS_CONCATENATION_KEY));
	}

	public void publish() throws IOException {
		
		// get all css resources from the landing page
		StringBuffer b = new StringBuffer();
		
		for (Media m : page.getResources()) {
			if (m.getMimetype().equalsIgnoreCase("text/css") || Media.getExtension(m).equalsIgnoreCase("css")) {
				
				// concat to one single script or append in a list
				if (concatenationEnabled) {
					b.append(new String(m.getData())).append("\n\n");
				} else {
					cssSources.put(m.getId(), new String(m.getData()));
				}
			}
		}
		
		// put the buffer into the list
		if (concatenationEnabled && b.length() == 0) 
			return;
	
		if (concatenationEnabled) 
			this.cssSources.put(new ObjectId(), b.toString());
	
		for (ObjectId id : this.cssSources.keySet()) {
			
			// adjust image links and collect images
			String s = this.cssSources.get(id);
			s = adjustCSSLinks(s);
			
			// flush each resource to a file
			FileOutputStream fOut = new FileOutputStream(this.cssResources.getAbsolutePath() + "/" + id + ".css");
			fOut.write(s.getBytes());
			fOut.flush();
			fOut.close();
			
			String link = "./resources/css/" + id + ".css";
			this.cssLinks.add(link);
		}
	}

	public List<Media> getImageResources() {
		return this.images;
	}
	
	public List<String> getCSSLinks() {
		return this.cssLinks;
	}
	
	private String adjustCSSLinks(String css) {
	
		// css pattern for url() styled expressions
		Pattern p = Pattern.compile("url\\(['|\"]?([a-zA-Z0-9\\./\\?\\-]*)['|\"]?\\)");
		Matcher m = p.matcher(css);
		
		while (m.find()) {
			// group 1 is to be replaced
			try {
				ObjectId mId = new ObjectId(m.group(1));
				Media media = mService.getMediaById(mId);
				this.images.add(media);
				css = m.replaceFirst("url(\"../img/$1."+ Media.getExtension(media) +"\")");
			} catch (Exception ex) {
				log.error("Unable to parse UUID from String : " + m.group(1));
			}
		}
		return css;
	}
}
