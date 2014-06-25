package de.mfischbo.bustamail.mailinglist.dto;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.dto.BaseDTO;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.SourceType;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.subscriber.dto.EMailAddressDTO;

public class SubscriptionDTO extends BaseDTO {

	private static final long serialVersionUID = -1842232934562158226L;

	private DateTime		dateCreated;
	private SourceType		sourceType;
	private State			state;
	private String			ipAddress;
	private EMailAddressDTO emailAddress;
	
	public SubscriptionDTO() {
		
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public EMailAddressDTO getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(EMailAddressDTO emailAddress) {
		this.emailAddress = emailAddress;
	}
}
