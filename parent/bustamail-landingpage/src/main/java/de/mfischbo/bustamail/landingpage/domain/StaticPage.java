package de.mfischbo.bustamail.landingpage.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;

@Entity
@Table(name = "LandingPages_StaticPage")
public class StaticPage extends BaseDomain implements HTMLPage {

	private static final long serialVersionUID = 8737051906342406125L;

	@Basic
	private String name;
	
	@Basic
	@Column(length = 4096)
	private String description;
	
	@Basic
	private DateTime	dateCreated;
	
	@Basic
	private DateTime	dateModified;
	
	@ManyToOne
	@JoinColumn(name = "UserCreated_id", referencedColumnName = "id", nullable = false)
	private User		userCreated;
	
	@ManyToOne
	@JoinColumn(name = "UserModified_id", referencedColumnName = "id", nullable = false)
	private User		userModified;
	
	@ManyToOne
	@JoinColumn(name = "LandingPage_id", referencedColumnName = "id", nullable = false)
	private LandingPage		parent;
	
	@ManyToOne
	@JoinColumn(name = "Template_id", referencedColumnName = "id", nullable = false)
	private Template		template;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public DateTime getDateModified() {
		return dateModified;
	}

	public void setDateModified(DateTime dateModified) {
		this.dateModified = dateModified;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public User getUserModified() {
		return userModified;
	}

	public void setUserModified(User userModified) {
		this.userModified = userModified;
	}

	public LandingPage getParent() {
		return parent;
	}

	public void setParent(LandingPage parent) {
		this.parent = parent;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
}
