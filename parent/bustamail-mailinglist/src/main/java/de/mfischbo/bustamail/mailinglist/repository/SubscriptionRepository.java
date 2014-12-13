package de.mfischbo.bustamail.mailinglist.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.subscriber.domain.Contact;

public interface SubscriptionRepository extends MongoRepository<Subscription, ObjectId> {

	Page<Subscription> findAllBySubscriptionList(SubscriptionList list, Pageable page);

	@Query(" { 'subscriptionList' : ?0 } ")
	List<Subscription> findAllSubscriptions(SubscriptionList list);
	
	@Query(" { 'subscriptionList' : ?0, 'contact' : ?1 } ")
	Subscription findBySubscriptionListAndContact(SubscriptionList list, Contact contact);
	
	@Query(" { 'subscriptionList' : ?0, 'state' : ?1 } ")
	long countBySubscriptionListAndState(SubscriptionList list, State state);
}
