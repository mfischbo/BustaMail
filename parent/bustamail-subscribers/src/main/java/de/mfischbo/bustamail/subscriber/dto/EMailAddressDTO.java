package de.mfischbo.bustamail.subscriber.dto;

import de.mfischbo.bustamail.common.dto.BaseDTO;

public class EMailAddressDTO extends BaseDTO {

	private static final long serialVersionUID = 561295403122029381L;
	
	private String		localPart;
	private String		domainPart;
	
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
	
	public String getAddress() {
		return localPart + "@" + domainPart;
	}
}
