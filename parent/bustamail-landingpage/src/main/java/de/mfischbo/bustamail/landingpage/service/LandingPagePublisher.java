package de.mfischbo.bustamail.landingpage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.zeroturnaround.zip.ZipUtil;

import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.mailer.util.HTMLSourceProcessor;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.template.util.DefaultTemplateMarkers;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;

class LandingPagePublisher {

	public enum Mode {
		PREVIEW,
		LIVE,
		EXPORT
	};

	private static final String DOCUMENT_ROOT_KEY = "de.mfischbo.bustamail.env.apache.documentRoot";
	private static final String API_ENDPOINT_KEY  = "de.mfischbo.bustamail.env.apiEndpoint";
	private static final String PREVIEW_URL_KEY   = "de.mfischbo.bustamail.env.previewUrl";
	private static final String LIVE_URL_KEY	  = "de.mfischbo.bustamail.env.liveUrl";
	
	private Environment		env;

	private MediaService	mService;
	private VersionedContentRepository vcRepo;
	
	private LandingPage		page;
	private Mode			publishingMode;

	
	private File				basedir;
	private File				cssResources;
	private File				jsResources;
	private File				imgResources;
	private List<Media>			images;
	private Map<ObjectId, String>	linkMap;
	private Map<ObjectId, Document>	contents;
	
	private String				pageUrl;
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	public LandingPagePublisher(Environment env, VersionedContentRepository vcRepo, MediaService mService,
			LandingPage page, Mode publishingMode) {
		this.env = env;
		this.vcRepo = vcRepo;
		this.mService = mService;
		this.publishingMode = publishingMode;
		this.page = page;
		this.images = new LinkedList<Media>();
		this.linkMap = new HashMap<>();
		this.contents = new HashMap<>();
	}
	
	public void publish() {
		if (this.publishingMode == Mode.LIVE) {
		
			this.basedir = new File(env.getProperty(DOCUMENT_ROOT_KEY) + "/" + createPageName(page.getName()));
			this.pageUrl = env.getProperty(LIVE_URL_KEY) + "/" + createPageName(page.getName());
	
		} else if (this.publishingMode == Mode.PREVIEW) {
		
			basedir = new File(env.getProperty(DOCUMENT_ROOT_KEY) + "/preview_" + page.getId());
			this.pageUrl = env.getProperty(PREVIEW_URL_KEY) + "/preview_" + page.getId();
		
		} else if (this.publishingMode == Mode.EXPORT) {
			basedir = new File("/tmp/" + page.getId());
		}
		publishInternal();
	}
	
	
	public void unpublish() throws IOException {
		this.basedir = new File(env.getProperty(DOCUMENT_ROOT_KEY) + "/" + createPageName(page.getName()));
		if (this.basedir.exists()) {
			FileUtils.deleteDirectory(this.basedir);
		}
	}

	public String getPageUrl() {
		return this.pageUrl;
	}

	/**
	 * Returns a zip file for the whole landing page
	 * @return
	 * @throws IOException
	 */
	public InputStream getZippedFileStream() throws IOException {
		if (this.publishingMode != Mode.EXPORT)
			return null;
		
		File zipFile = new File("/tmp/" + page.getId() + ".zip");
		ZipUtil.pack(basedir, zipFile);
		return new FileInputStream(zipFile);
	}
	
	private void publishInternal() {
		
		// step 0 : Create a directory in the document root
		log.debug("Creating initial preview basedirectory for landing page : " + page.getName());
		
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
		VersionedContent pageContent = getMostRecentVersionById(page.getId());
		
		
		// step 2: Content only contains the body. Wrap up in stub HTML document and create a jsoup from it
		StringBuffer b = new StringBuffer(page.getHtmlHeader()).append("<body>").append(pageContent.getContent()).append("</body></html>");
		Document d = Jsoup.parse(b.toString());
		d = prepareForms(d);
		this.contents.put(page.getId(), d);
		this.linkMap.put(page.getId(), "index");
		
		
		for (StaticPage subPage : page.getStaticPages()) {
			VersionedContent content = getMostRecentVersionById(subPage.getId());
			b = new StringBuffer(page.getHtmlHeader()).append("<body>").append(content.getContent()).append("</body></html>");
			Document subD = Jsoup.parse(b.toString());
			
			subD = collectImages(subD);
			this.contents.put(subPage.getId(), subD);
			this.linkMap.put(subPage.getId(), subPage.getName());
		}
		
		// step 3: Add all css/js resources from the template to the html head
		for (ObjectId id : this.contents.keySet()) {
			Document c = this.contents.get(id);
			Element head = c.getElementsByTag("head").first();
		
			try {
				CSSFilePublisher cPub = new CSSFilePublisher(env, cssResources, mService, page);
				cPub.publish();
				this.images.addAll(cPub.getImageResources());
				JSFilePublisher jsPub = new JSFilePublisher(env, jsResources, page);
				jsPub.publish();
				
				for (String cssLink : cPub.getCSSLinks()) 
					head.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssLink + "\">");
				for (String jsLink : jsPub.getJSLinks()) 
					head.append("<script type=\"text/javascript\" src=\"" + jsLink + "\"></script>");
			} catch (Exception ex) {
				log.error("Failed to publish static resources. Cause: " + ex.getMessage());
			}
		}
	
		// step 4: Collect all images from the html document and the css files
		d = collectImages(d);
		this.linkMap.put(this.page.getId(), "index");
		
		// link all documents
		for (Document c : contents.values()) 
			c = replaceHyperlinks(c);
	
		// step 5: write all collected image files
		for (Media m : images) {
			try {
				FileOutputStream fOut = new FileOutputStream(
						new File(basedir.getAbsolutePath() + "/resources/img/" + m.getId() + "." + Media.getExtension(m)));
				fOut.write(m.getData());
				fOut.flush();
				fOut.close();
			} catch (Exception ex) {
				
			}
		}
	
		// step 7 : Write the landing page
		for (ObjectId id : contents.keySet()) {
			Document c = contents.get(id);
			
			// remove editor stuff
			c = HTMLSourceProcessor.removeAttributes(c, DefaultTemplateMarkers.getTemplateAttributeMarkers());
			c = HTMLSourceProcessor.removeClasses(c, DefaultTemplateMarkers.getTemplateClassMarkers());
			
			// set an appropriate page title
			c.select("head title").first().text(page.getName());
	
			try {
				String name = createPageName(linkMap.get(id) + ".html");
				log.info("Writing contents for page : " + name);
				FileOutputStream fOut = new FileOutputStream(new File(basedir.getAbsolutePath() + "/" + name));
				fOut.write(c.html().getBytes());
				fOut.flush();
				fOut.close();
			} catch (Exception ex) {
				log.error("Unable to publish page. Cause: " + ex.getMessage());
			}
		}
	}
	

	// collects the images of a HTML document and rewrites the links appropriately
	private Document collectImages(Document d) {
		for (Element e : d.getElementsByTag("img")) {
			String src = e.attr("src");
			try {
				if (src.startsWith("./img/media/"))
					src = src.replaceAll("./img/media/", "");
				//UUID mId = UUID.fromString(src);

				ObjectId mId = new ObjectId(src);
				Media m = mService.getMediaById(mId);
				images.add(m);
				e.attr("src", "./resources/img/" + m.getId() + "." + Media.getExtension(m));
			} catch (Exception ex) {
				log.error("Unable to parse UUID from String : " + src);
			}
		}
		return d;
	}


	private Document replaceHyperlinks(Document d) {
		Elements els = d.getElementsByTag("a");
		for (Element e : els) {
			try {
				UUID id = UUID.fromString(e.attr("href"));
				e.attr("href", createPageName(linkMap.get(id) + ".html"));
			} catch (Exception ex) {
				// this is a external link... ignore it
			}
		}
		return d;
	}
	

	private String createPageName(String uiName) {
		uiName = uiName.toLowerCase();
		uiName = uiName.replaceAll(" ", "-");
		uiName = uiName.replaceAll("[^\\x20-\\x7e]", "");
		//uiName += ".html";
		return uiName;
	}
	
	private VersionedContent getMostRecentVersionById(ObjectId id) {
		PageRequest preq = new PageRequest(0,1, Sort.Direction.DESC, "dateCreated");
		VersionedContent pageContent = vcRepo.findByForeignId(id, preq).getContent().get(0);
		return pageContent;
	}
	
	private Document prepareForms(Document d) {
		Elements elms = d.getElementsByTag("form");
		for (Element e : elms) {
			String fName = e.attr("name");
			if (fName == null || fName.isEmpty())
				continue;
			
			LPForm targetForm = null;
			for (LPForm form : this.page.getForms()) {
				if (form.getName().equals(fName)) {
					targetForm = form;
					break;
				}
			}
			
			if (targetForm != null) {
				e.attr("method", "POST");
				e.attr("action", env.getProperty(API_ENDPOINT_KEY) + "/forms/" + targetForm.getId());
			}
		}
		return d;
	}
}
