package de.mfischbo.bustamail.mailinglist.repository;

import java.util.UUID;

import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.security.repository.OwnerJpaRepository;

public interface SubscriptionListRepository extends
		OwnerJpaRepository<SubscriptionList, UUID> {

}
