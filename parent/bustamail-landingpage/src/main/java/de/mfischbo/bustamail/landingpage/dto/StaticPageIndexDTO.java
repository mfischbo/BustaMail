package de.mfischbo.bustamail.landingpage.dto;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.template.dto.TemplateIndexDTO;

public class StaticPageIndexDTO extends BaseDomain {

	private static final long serialVersionUID = 2727341025712066827L;
	private String name;
	private String description;
	private DateTime				dateCreated;
	private DateTime				dateModified;
	private UserDTO					userCreated;
	private UserDTO					userModified;
	private TemplateIndexDTO		template;
	
	
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
	public UserDTO getUserCreated() {
		return userCreated;
	}
	public void setUserCreated(UserDTO userCreated) {
		this.userCreated = userCreated;
	}
	public UserDTO getUserModified() {
		return userModified;
	}
	public void setUserModified(UserDTO userModified) {
		this.userModified = userModified;
	}
	public TemplateIndexDTO getTemplate() {
		return template;
	}
	public void setTemplate(TemplateIndexDTO template) {
		this.template = template;
	}
}
