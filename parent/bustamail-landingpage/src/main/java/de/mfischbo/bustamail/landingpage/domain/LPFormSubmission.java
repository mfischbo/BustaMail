package de.mfischbo.bustamail.landingpage.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "LandingPage_FormSubmission")
public class LPFormSubmission extends BaseDomain {

	private static final long serialVersionUID = -398673080662837903L;

	@Basic
	private DateTime		dateCreated;

	@Basic
	@Column(length = 32)
	private String			sourceIP;
	
	@Basic
	@Column(length = 4096)
	private String			data;

	@ManyToOne(optional = false)
	@JoinColumn(name = "LPForm_id", referencedColumnName = "id")
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
