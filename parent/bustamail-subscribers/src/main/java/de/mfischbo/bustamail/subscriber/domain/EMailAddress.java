package de.mfischbo.bustamail.subscriber.domain;

import org.hibernate.validator.constraints.NotBlank;

import de.mfischbo.bustamail.common.domain.BaseDomain;

public class EMailAddress extends BaseDomain {

	private static final long serialVersionUID = 8055176368450793272L;

	@NotBlank
	private String localPart;
	
	@NotBlank
	private String domainPart;
	

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

	@Override
	public String toString() {
		return localPart + "@" + domainPart;
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
