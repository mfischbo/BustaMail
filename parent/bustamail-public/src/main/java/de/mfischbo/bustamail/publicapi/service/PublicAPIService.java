package de.mfischbo.bustamail.publicapi.service;

import java.util.List;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.publicapi.dto.PublicSubscription;

public interface PublicAPIService {

	public List<SubscriptionList> getPublicSubscriptionListByOrgUnit(ObjectId id, boolean deep);
	
	public PublicSubscription getSubscriptionById(ObjectId id, String email);
	
	public PublicSubscription createSubscription(PublicSubscription subscription);
	
	public PublicSubscription updateSubscription(PublicSubscription subscription);

	public PublicSubscription activateSubscription(PublicSubscription subscription);
	
	public void deleteSubscripton(PublicSubscription subscription);
}
