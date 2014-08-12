package de.mfischbo.bustamail.landingpage.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;

@Entity
@Table(name = "LandingPage_LandingPage")
public class LandingPage extends OwnedBaseDomain {

	private static final long serialVersionUID = 1686708887179244769L;

	@Basic
	@NotBlank
	private String name;
	
	@Basic
	@Column(length = 4096)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "Template_id", referencedColumnName = "id", nullable=false)
	private Template template;
	
	@ManyToOne
	@JoinColumn(name = "UserCreated_id", referencedColumnName = "id", nullable=false)
	private User		userCreated;
	
	@ManyToOne
	@JoinColumn(name = "UserModified_id", referencedColumnName = "id", nullable = false)
	private User		userModified;
	
	@ManyToOne
	@JoinColumn(name = "UserPublished_id", referencedColumnName = "id", nullable = true)
	private User		userPublished;
	
	@Basic
	private DateTime	dateCreated;

	@Basic
	private DateTime	dateModified;

	@Basic
	private DateTime	datePublished;
	
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

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
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

	public User getUserPublished() {
		return userPublished;
	}

	public void setUserPublished(User userPublished) {
		this.userPublished = userPublished;
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

	public DateTime getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(DateTime datePublished) {
		this.datePublished = datePublished;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}