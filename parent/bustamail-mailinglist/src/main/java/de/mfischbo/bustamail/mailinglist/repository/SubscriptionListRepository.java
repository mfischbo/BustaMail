package de.mfischbo.bustamail.mailinglist.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface SubscriptionListRepository extends
		OwnerMongoRepository<SubscriptionList, ObjectId> {

	Page<SubscriptionList> findByOwnerAndNameLike(OrgUnit owner, String query, Pageable page);
}
