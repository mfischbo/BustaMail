package de.mfischbo.bustamail.landingpage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.env.Environment;

import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.media.domain.Media;

class JSFilePublisher {

	private static final String JS_CONCATENATION_KEY = "de.mfischbo.bustamail.landingpages.publisher.enableJSConcatenation";
	
	private File		jsResources;
	private List<String> jsLinks;
	private Map<UUID, String> sources;
	private LandingPage	page;
	private boolean		concatEnabled;
	
	public JSFilePublisher(Environment env, File jsResources, LandingPage page) {
		this.jsResources = jsResources;
		this.jsLinks = new LinkedList<>();
		this.concatEnabled = Boolean.parseBoolean(env.getProperty(JS_CONCATENATION_KEY));
		this.sources = new HashMap<>();
		this.page = page;
	}
	
	public void publish() throws IOException {
		
		StringBuffer b = new StringBuffer();
		
		for (Media m : page.getResources()) {
			if (m.getMimetype().equalsIgnoreCase("text/javascript") || m.getExtension().equalsIgnoreCase("js")) {
				if (concatEnabled) {
					b.append(new String(m.getData())).append("\n\n");
				} else {
					this.sources.put(m.getId(), new String(m.getData()));
				}
			}
		}
		
		if (concatEnabled && b.length() == 0)
			return;
		if (concatEnabled)
			this.sources.put(UUID.randomUUID(), b.toString());
			
		for (UUID id : this.sources.keySet()) {
			String s = this.sources.get(id);
		
			FileOutputStream fOut = new FileOutputStream(this.jsResources.getAbsolutePath() + "/" + id + ".js");
			fOut.write(s.getBytes());
			fOut.flush();
			fOut.close();
			
			String link = "./resources/js/" + id + ".js";
			this.jsLinks.add(link);
		}
	}
	
	public List<String> getJSLinks() {
		return this.jsLinks;
	}
}
