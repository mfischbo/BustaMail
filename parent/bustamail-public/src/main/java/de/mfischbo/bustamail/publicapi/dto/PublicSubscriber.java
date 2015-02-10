package de.mfischbo.bustamail.publicapi.dto;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.common.domain.Gender;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.subscriber.domain.Address;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public class PublicSubscriber {

	public static class PubSubscription {

		public PubSubscription() {
			
		}
		
		public PubSubscription(SubscriptionList list) {
			this.id = list.getId();
			this.name = list.getName();
		}
		
		private ObjectId id;
		private String   name;
	
		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	private ObjectId	id;
	private String title;
	private Gender gender;
	private String firstName;
	private String lastName;
	private boolean formalSalutation;
	private List<Address> addresses = new ArrayList<>();
	private List<EMailAddress> emailAddresses = new ArrayList<>();
	private List<PubSubscription> subscriptions = new ArrayList<>();
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean isFormalSalutation() {
		return formalSalutation;
	}
	public void setFormalSalutation(boolean formalSalutation) {
		this.formalSalutation = formalSalutation;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	public List<EMailAddress> getEmailAddresses() {
		return emailAddresses;
	}
	public void setEmailAddresses(List<EMailAddress> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	public List<PubSubscription> getSubscriptions() {
		return subscriptions;
	}
	public void setSubscriptions(List<PubSubscription> subscriptions) {
		this.subscriptions = subscriptions;
	}
}
