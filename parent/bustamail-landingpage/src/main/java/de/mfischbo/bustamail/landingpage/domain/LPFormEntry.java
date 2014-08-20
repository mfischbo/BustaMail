package de.mfischbo.bustamail.landingpage.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.validator.constraints.NotBlank;

@Embeddable
public class LPFormEntry implements Serializable {

	private static final long serialVersionUID = 6800401468137187935L;

	public enum ValidationType {
		NOT_EMPTY,
		EMAIL,
		DATE,
		INTEGER,
		FLOAT,
		REGEXP
	}
	
	@NotBlank
	@Basic(optional = false)
	private String name;
	
	@Basic
	private boolean required;
	
	@Basic
	@Enumerated(EnumType.STRING)
	private ValidationType	validationType;

	@Basic
	@Column(name = "RegularExpression")
	private String			regexp;
	
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
