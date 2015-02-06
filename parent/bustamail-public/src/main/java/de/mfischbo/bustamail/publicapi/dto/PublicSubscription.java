package de.mfischbo.bustamail.publicapi.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.mfischbo.bustamail.common.dto.BaseDTO;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.subscriber.domain.Contact;

public class PublicSubscription extends BaseDTO {

	private static final long serialVersionUID = 1913548695654543079L;

	@NotNull
	private Contact contact;
	
	@NotNull
	@Size(min=1)
	private List<SubscriptionList> subscriptions;

	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public List<SubscriptionList> getSubscriptions() {
		return subscriptions;
	}
	public void setSubscriptions(List<SubscriptionList> subscriptions) {
		this.subscriptions = subscriptions;
	}
}
