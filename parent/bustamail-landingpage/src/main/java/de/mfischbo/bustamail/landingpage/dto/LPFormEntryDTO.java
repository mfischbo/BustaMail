package de.mfischbo.bustamail.landingpage.dto;

import java.io.Serializable;

import de.mfischbo.bustamail.landingpage.domain.LPFormEntry.ValidationType;

public class LPFormEntryDTO implements Serializable {

	private static final long serialVersionUID = -450115717507519104L;
	
	private String name;
	private boolean required;
	private ValidationType	validationType;
	private String regexp;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public ValidationType getValidationType() {
		return validationType;
	}
	public void setValidationType(ValidationType validationType) {
		this.validationType = validationType;
	}
	public String getRegexp() {
		return regexp;
	}
	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
}
