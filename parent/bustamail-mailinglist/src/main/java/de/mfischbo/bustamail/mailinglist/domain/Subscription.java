package de.mfischbo.bustamail.mailinglist.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.common.domain.Gender;
import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

@Document(collection = "MailingList_Subscription")
public class Subscription extends BaseDomain implements PersonalizedEmailRecipient {

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

	@Override
	@Transient
	public Gender getGender() {
		if (contact == null)
			return Gender.N;
		return contact.getGender();
	}

	@Override
	@Transient
	public String getFirstName() {
		if (contact == null)
			return "";
		return contact.getFirstName();
	}

	@Override
	@Transient
	public String getLastName() {
		if (contact == null)
			return "";
		return contact.getLastName();
	}

	@Override
	@Transient
	public boolean hasFormalSalutation() {
		if (contact == null)
			return true;
		return contact.isFormalSalutation();
	}

	@Override
	@Transient
	public String getEmail() {
		return emailAddress.toString();
	}
}
