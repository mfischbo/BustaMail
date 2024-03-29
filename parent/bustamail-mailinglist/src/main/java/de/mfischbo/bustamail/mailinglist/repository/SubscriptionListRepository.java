package de.mfischbo.bustamail.mailinglist.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface SubscriptionListRepository extends
		OwnerMongoRepository<SubscriptionList, ObjectId> {

	Page<SubscriptionList> findByOwnerAndNameLike(OrgUnit owner, String query, Pageable page);
	
	@Query(" { 'owner' : ?0, 'publiclyAvailable' : true } ")
	List<SubscriptionList> findAllPublicByOwner(ObjectId owner);
	
	@Query(" { 'publiclyAvailable' : true, 'owner' : { $in : ?0 } } ")
	List<SubscriptionList> findAllPublicByOwners(List<ObjectId> owners);
	
	@Query(" { 'publiclyAvailable' : true, '_id' : { $in : ?0 } }")
	List<SubscriptionList> findAllPublicByIds(List<ObjectId> ids);
}
