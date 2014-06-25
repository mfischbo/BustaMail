package de.mfischbo.bustamail.subscriber.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "Subscriber_EMailAddress")
public class EMailAddress extends BaseDomain {

	@Basic
	@Column(length = 255)
	private String localPart;
	
	@Basic
	@Column(length = 255)
	private String domainPart;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "Contact_id", referencedColumnName = "id")
	private Contact	contact;

	public String getLocalPart() {
		return localPart;
	}

	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}

	public String getDomainPart() {
		return domainPart;
	}

	public void setDomainPart(String domainPart) {
		this.domainPart = domainPart;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
}
