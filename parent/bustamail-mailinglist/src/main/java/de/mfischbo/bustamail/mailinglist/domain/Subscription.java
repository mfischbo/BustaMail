package de.mfischbo.bustamail.mailinglist.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.subscriber.domain.Contact;

@Entity
@Table(name = "SubscriptionList_Subscription")
public class Subscription extends BaseDomain {

	public enum SourceType {
		UserAction,
		ImportAction,
		FormAction
	}
	
	@Basic(optional = false)
	private DateTime				dateCreated;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	private SourceType				sourceType;
	
	
	@Basic(optional = true)
	private String					ipAddress;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "SubscriptionList_id", referencedColumnName = "id")
	private	SubscriptionList		subscriptionList;

	@OneToOne(optional = false)
	@JoinColumn(name = "Contact_id", referencedColumnName = "id")
	private Contact					contact;

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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public SubscriptionList getSubscriptionList() {
		return subscriptionList;
	}

	public void setSubscriptionList(SubscriptionList subscriptionList) {
		this.subscriptionList = subscriptionList;
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
		int result = 1;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result
				+ ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result
				+ ((sourceType == null) ? 0 : sourceType.hashCode());
		result = prime
				* result
				+ ((subscriptionList == null) ? 0 : subscriptionList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subscription other = (Subscription) obj;
		if (contact == null) {
			if (other.contact != null)
				return false;
		} else if (!contact.equals(other.contact))
			return false;
		if (dateCreated == null) {
			if (other.dateCreated != null)
				return false;
		} else if (!dateCreated.equals(other.dateCreated))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (sourceType != other.sourceType)
			return false;
		if (subscriptionList == null) {
			if (other.subscriptionList != null)
				return false;
		} else if (!subscriptionList.equals(other.subscriptionList))
			return false;
		return true;
	}
}
