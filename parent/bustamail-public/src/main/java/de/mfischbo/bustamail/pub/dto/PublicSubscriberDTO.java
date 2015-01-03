package de.mfischbo.bustamail.pub.dto;

import java.util.List;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.subscriber.domain.Contact;

public class PublicSubscriberDTO {

	private ObjectId id;
	private Contact  contact;
	
	private List<SubscriptionList> subscriptionLists;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public List<SubscriptionList> getSubscriptionLists() {
		return subscriptionLists;
	}

	public void setSubscriptionLists(List<SubscriptionList> subscriptionLists) {
		this.subscriptionLists = subscriptionLists;
	}
}
