package de.mfischbo.bustamail.mailinglist.domain;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

@Document(collection = "MailingList_Subscription")
public class Subscription extends BaseDomain {

	private static final long serialVersionUID = 3316987035776229064L;

	public enum SourceType {
		UserAction,
		ImportAction,
		FormAction
	}
	
	public enum State {
		ACTIVE,
		OPTIN,
		INACTIVE
	}
	
	private DateTime				dateCreated;

	private SourceType				sourceType;
	
	private State					state;
	
	private String					ipAddress;
	
	@DBRef
	private Contact					contact;

	private EMailAddress			emailAddress;
	
	@DBRef
	private SubscriptionList		subscriptionList;
	
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

	public EMailAddress getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(EMailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public SubscriptionList getSubscriptionList() {
		return subscriptionList;
	}

	public void setSubscriptionList(SubscriptionList subscriptionList) {
		this.subscriptionList = subscriptionList;
	}
}
