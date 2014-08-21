package de.mfischbo.bustamail.landingpage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.mailer.util.HTMLSourceProcessor;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.template.util.DefaultTemplateMarkers;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;
import de.mfischbo.bustamail.vc.repo.VersionedContentSpecification;

class LandingPagePublisher {

	public enum Mode {
		PREVIEW,
		LIVE
	};

	private static final String DOCUMENT_ROOT_KEY = "de.mfischbo.bustamail.env.apache.documentRoot";
	
	private Environment		env;

	private MediaService	mService;
	private VersionedContentRepository vcRepo;
	
	private LandingPage		page;
	private Mode			publishingMode;

	
	private File			basedir;
	private File			cssResources;
	private File			jsResources;
	private File			imgResources;
	private List<Media>		images;
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	public LandingPagePublisher(Environment env, VersionedContentRepository vcRepo, MediaService mService,
			LandingPage page, Mode publishingMode) {
		this.env = env;
		this.vcRepo = vcRepo;
		this.mService = mService;
		this.publishingMode = publishingMode;
		this.page = page;
		this.images = new LinkedList<Media>();
	}
	
	public void publish() {
		if (this.publishingMode == Mode.PREVIEW)
			this.publishPreview();
		else 
			this.publishLive();
		
	}

	private void publishLive() {
		
	}
	
	private void publishPreview() {
		
		// step 0 : Create a directory in the document root
		log.debug("Creating initial preview basedirectory for landing page : " + page.getName());
		basedir = new File(env.getProperty(DOCUMENT_ROOT_KEY) + "/preview_" + page.getId());
		
		if (basedir.exists()) {
			// if directory exits wipe it
			File[] contents = basedir.listFiles();
			for (File f : contents) {
				f.delete();
			}
		} else {
			// first preview: create base directory 
			basedir.mkdir();
		}
		
		// create the initial directory structure 
		log.debug("Creating initial directory structure for landing page preview");
		cssResources = new File(basedir.getAbsolutePath() + "/resources/css");
		cssResources.mkdirs();	
		jsResources = new File(basedir.getAbsolutePath() + "/resources/js");
		jsResources.mkdirs();
		imgResources = new File(basedir.getAbsolutePath() + "/resources/img");
		imgResources.mkdirs();
		
		
		// step 1: Read recent content version for the landing page
		Specification<VersionedContent> specs = Specifications.where(VersionedContentSpecification.mailingIdIs(page.getId()));
		PageRequest preq = new PageRequest(0,1, Sort.Direction.DESC, "dateCreated");
		VersionedContent pageContent = vcRepo.findAll(specs, preq).getContent().get(0);
		
		// step 2: Content only contains the body. Wrap up in stub HTML document and create a jsoup from it
		StringBuffer b = new StringBuffer("<!DOCTYPE html><html><head></head><body>").append(pageContent.getContent()).append("</body></html>");
		Document d = Jsoup.parse(b.toString());
		
		// step 3: Add all css/js resources from the template to the html head
		Element head = d.getElementsByTag("head").first();
		for (Media m : page.getTemplate().getResources()) {
			try {
				head.append(getLinkForMedia(basedir, m));
			} catch (Exception ex) {
				log.error("Unable to publish media file : " + m.getName() + ". Cause: " + ex.getMessage());
			}
		}
		
		// step 4: Collect all images from the html document and the css files
		d = collectImages(d);
		
		// step 5: write all collected image files
		for (Media m : images) {
			try {
				FileOutputStream fOut = new FileOutputStream(new File(basedir.getAbsolutePath() + "/resources/img/" + m.getId() + "." + m.getExtension()));
				fOut.write(m.getData());
				fOut.flush();
				fOut.close();
			} catch (Exception ex) {
				
			}
		}
		
		// step 6: remove editor related classes and attributes
		d = HTMLSourceProcessor.removeAttributes(d, DefaultTemplateMarkers.getTemplateAttributeMarkers());
		d = HTMLSourceProcessor.removeClasses(d, DefaultTemplateMarkers.getTemplateClassMarkers());
		
		// step 4 : Write to basedir as index.html
		try {
			FileOutputStream fOut = new FileOutputStream(new File(basedir.getAbsolutePath() + "/index.html"));
			fOut.write(d.html().getBytes());
			fOut.flush();
			fOut.close();
		} catch (Exception ex) {
			log.error("Unable to publish page. Cause: " + ex.getMessage());
		}
	}
	
	private String getLinkForMedia(File basedir, Media m) throws IOException {
		FileOutputStream fOut = null;
		String retval = null;
		
		if (m.getMimetype().equals("text/css") || m.getExtension().equals("css")) {
			StringBuffer b = new StringBuffer("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			b.append("./resources/css/").append(m.getId()).append(".css");
			b.append("\">");
			fOut = new FileOutputStream(cssResources.getAbsolutePath() + "/" + m.getId() + ".css");
			
			String f = new String(m.getData());
			f = adjustCSSLinks(f);
			fOut.write(f.getBytes());
			fOut.flush();
			fOut.close();
			retval = b.toString();
		}
		
		if (m.getMimetype().equals("text/javascript") || m.getExtension().equals("js")) {
			StringBuffer b = new StringBuffer("<script type=\"text/javascript\" src=\"");
			b.append("./resources/js/").append(m.getId()).append(".js");
			b.append("\"></script>");
			fOut = new FileOutputStream(jsResources.getAbsolutePath() + "/" + m.getId() + ".js");
			retval = b.toString();
		}
		return retval;
	}
	
	// collects the images of a HTML document and rewrites the links appropriately
	private Document collectImages(Document d) {
		for (Element e : d.getElementsByTag("img")) {
			String src = e.attr("src");
			try {
				if (src.startsWith("./img/media/"))
					src = src.replaceAll("./img/media/", "");
				UUID mId = UUID.fromString(src);
				Media m = mService.getMediaById(mId);
				images.add(m);
				e.attr("src", "./resources/img/" + m.getId() + "." + m.getExtension());
			} catch (Exception ex) {
				log.error("Unable to parse UUID from String : " + src);
			}
		}
		return d;
	}
	
	private String adjustCSSLinks(String css) {
	
		// css pattern for url() styled expressions
		Pattern p = Pattern.compile("url\\(['|\"]?([a-zA-Z0-9\\./\\?\\-]*)['|\"]?\\)");
		Matcher m = p.matcher(css);
		
		while (m.find()) {
			// group 1 is to be replaced
			try {
				UUID mId = UUID.fromString(m.group(1));
				Media media = mService.getMediaById(mId);
				this.images.add(media);
				css = m.replaceFirst("url(\"../img/$1."+ media.getExtension() +"\")");
			} catch (Exception ex) {
				log.error("Unable to parse UUID from String : " + m.group(1));
			}
		}
		return css;
	}
}
