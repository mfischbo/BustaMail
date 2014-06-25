package de.mfischbo.bustamail.mailinglist.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

	Page<Subscription> findAllBySubscriptionList(SubscriptionList list, Pageable page);
}
