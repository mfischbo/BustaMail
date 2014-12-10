package de.mfischbo.bustamail.mailinglist.dto;

import java.io.Serializable;

public class SubscriptionListSummaryDTO implements Serializable {

	private static final long serialVersionUID = -7514138829740579954L;
	
	private long		subscriptionsActive;
	private long		subscriptionsPending;
	private long		subscriptionsInactive;
	
	public SubscriptionListSummaryDTO() {
		
	}

	public long getSubscriptionsActive() {
		return subscriptionsActive;
	}

	public void setSubscriptionsActive(long subscriptionsActive) {
		this.subscriptionsActive = subscriptionsActive;
	}

	public long getSubscriptionsPending() {
		return subscriptionsPending;
	}

	public void setSubscriptionsPending(long subscriptionsPending) {
		this.subscriptionsPending = subscriptionsPending;
	}

	public long getSubscriptionInactive() {
		return subscriptionsInactive;
	}

	public void setSubscriptionsInactive(long subscriptionInactive) {
		this.subscriptionsInactive = subscriptionInactive;
	}
}
