package de.mfischbo.bustamail.landingpage.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;

@MappedSuperclass
public class AbstractHtmlPage extends OwnedBaseDomain {

	private static final long serialVersionUID = -7893494702110662322L;

	@Basic
	@NotBlank
	private String name;
	
	@Basic
	@Column(length = 4096)
	private String description;
	
	@Basic
	@NotNull
	private DateTime dateCreated;
	
	@Basic
	@NotNull
	private DateTime dateModified;
	
	@ManyToOne
	@JoinColumn(name = "UserCreated_id", referencedColumnName = "id", nullable = false)
	private User		userCreated;
	
	@ManyToOne
	@JoinColumn(name = "UserModified_id", referencedColumnName = "id", nullable = false)
	private User		userModified;

	@ManyToOne
	@JoinColumn(name = "Template_id", referencedColumnName = "id", nullable=false)
	private Template 	template;

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

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
}
