package de.mfischbo.bustamail.landingpage.domain;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;

import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.security.domain.User;

public class LandingPage extends AbstractHtmlPage implements HTMLPage {

	private static final long serialVersionUID = 1686708887179244769L;

	private DateTime	datePublished;

	private String		pageUrl;

	private boolean		published;
	
	private List<StaticPage>	staticPages;
	
	private List<LPForm>	forms;

	private List<Media>	resources;
	
	@DBRef
	private User		userPublished;

	
	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public User getUserPublished() {
		return userPublished;
	}

	public void setUserPublished(User userPublished) {
		this.userPublished = userPublished;
	}

	public DateTime getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(DateTime datePublished) {
		this.datePublished = datePublished;
	}
	
	public List<StaticPage> getStaticPages() {
		return staticPages;
	}

	public void setStaticPages(List<StaticPage> staticPages) {
		this.staticPages = staticPages;
	}

	public List<LPForm> getForms() {
		return forms;
	}

	public void setForms(List<LPForm> forms) {
		this.forms = forms;
	}
	
	public List<Media> getResources() {
		return resources;
	}

	public void setResources(List<Media> resources) {
		this.resources = resources;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
}