package de.mfischbo.bustamail.publicapi.service;

import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.exception.ApiException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.publicapi.dto.PublicSubscriber;

public interface PublicAPIService {

	public List<SubscriptionList> getPublicSubscriptionListByOrgUnit(ObjectId id, boolean deep);

	public PublicSubscriber getSubscriberById(ObjectId id, String email) throws EntityNotFoundException;
	
	public PublicSubscriber createSubscriber(PublicSubscriber subscriber, String sourceIP) throws ApiException;
	
	public PublicSubscriber updateSubscriber(PublicSubscriber subscriber, String email) throws EntityNotFoundException;
	
	public void				deleteSubscriber(PublicSubscriber subscriber, String email) throws EntityNotFoundException;
	
	public PublicSubscriber createSubscriptions(PublicSubscriber subscriber, String email, List<ObjectId> listIds, String ipAddr) throws EntityNotFoundException;
	
	public PublicSubscriber deleteSubscriptions(PublicSubscriber subscriver, String email, List<ObjectId> listIds) throws EntityNotFoundException;
	
	public void				activateSubscriptions(UUID transactionId);
}
