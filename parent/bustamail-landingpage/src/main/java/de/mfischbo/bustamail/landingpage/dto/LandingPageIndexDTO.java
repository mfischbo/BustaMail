package de.mfischbo.bustamail.landingpage.dto;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.dto.OwnedBaseDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.template.dto.TemplateIndexDTO;

public class LandingPageIndexDTO extends OwnedBaseDTO {

	private static final long serialVersionUID = -7933166616560293119L;
	private String name;
	private String description;
	
	private TemplateIndexDTO	template;
	private UserDTO userCreated;
	private UserDTO userModified;
	private UserDTO userPublished;
	private DateTime dateCreated;
	private DateTime dateModified;
	private DateTime datePublished;
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
	public TemplateIndexDTO getTemplate() {
		return template;
	}
	public void setTemplate(TemplateIndexDTO template) {
		this.template = template;
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
	public UserDTO getUserPublished() {
		return userPublished;
	}
	public void setUserPublished(UserDTO userPublished) {
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
	
	
}
