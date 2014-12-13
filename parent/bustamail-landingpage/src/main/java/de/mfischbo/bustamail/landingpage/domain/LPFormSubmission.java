package de.mfischbo.bustamail.landingpage.domain;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;

public class LPFormSubmission extends BaseDomain {

	private static final long serialVersionUID = -398673080662837903L;

	private DateTime		dateCreated;

	private String			sourceIP;
	
	private String			data;

	private LPForm			form;

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public LPForm getForm() {
		return form;
	}

	public void setForm(LPForm form) {
		this.form = form;
	}
}
