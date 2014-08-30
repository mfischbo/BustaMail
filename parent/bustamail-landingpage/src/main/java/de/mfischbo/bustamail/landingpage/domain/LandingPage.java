package de.mfischbo.bustamail.landingpage.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.security.domain.User;

@Entity
@Table(name = "LandingPage_LandingPage")
public class LandingPage extends AbstractHtmlPage implements HTMLPage {

	private static final long serialVersionUID = 1686708887179244769L;

	@Basic
	private DateTime	datePublished;
	
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<StaticPage>	staticPages;
	
	@OneToMany(mappedBy = "landingPage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<LPForm>	forms;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "LandingPage_LP_Resources",
		joinColumns 		= { @JoinColumn(name = "LandingPage_id", referencedColumnName = "id") },
		inverseJoinColumns  = { @JoinColumn(name = "Media_id", 		 referencedColumnName = "id") }
	)
	private List<Media>	resources;
	
	@ManyToOne
	@JoinColumn(name = "UserPublished_id", referencedColumnName = "id", nullable = true)
	private User		userPublished;
	

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
}