package de.mfischbo.bustamail.subscriber.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "Subscriber_EMailAddress", indexes = {
		@Index(name = "MailIndex", columnList = "localPart,domainPart")
})
public class EMailAddress extends BaseDomain {

	@Basic
	@Column(name = "localPart", length = 255, nullable = false)
	@NotBlank
	private String localPart;
	
	@Basic
	@Column(name = "domainPart", length = 255, nullable = false)
	@NotBlank
	private String domainPart;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "Contact_id", referencedColumnName = "id")
	private Contact	contact;

	public EMailAddress() {
		
	}
	
	public EMailAddress(String address) {
		assert(address != null);
		String[] s = address.split("@");
		assert(s.length == 2);
		this.localPart = s[0];
		this.domainPart = s[1];
	}
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * 1 
				+ ((domainPart == null) ? 0 : domainPart.hashCode());
		result = prime * result
				+ ((localPart == null) ? 0 : localPart.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		EMailAddress other = (EMailAddress) obj;
		if (domainPart == null) {
			if (other.domainPart != null)
				return false;
		} else if (!domainPart.equals(other.domainPart))
			return false;
		if (localPart == null) {
			if (other.localPart != null)
				return false;
		} else if (!localPart.equals(other.localPart))
			return false;
		return true;
	}
}
