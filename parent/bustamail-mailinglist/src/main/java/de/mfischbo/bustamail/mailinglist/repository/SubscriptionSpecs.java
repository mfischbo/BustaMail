package de.mfischbo.bustamail.mailinglist.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.domain.Subscription_;
import de.mfischbo.bustamail.subscriber.domain.Contact;

public class SubscriptionSpecs {

	public static Specification<Subscription> onList(final SubscriptionList list) {
		return new Specification<Subscription>() {

			@Override
			public Predicate toPredicate(Root<Subscription> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(Subscription_.subscriptionList), list);
			}
		};
	}
	
	
	public static Specification<Subscription> withAnyOfContactsEmails(final Contact c) {
		return new Specification<Subscription>() {

			@Override
			public Predicate toPredicate(Root<Subscription> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return root.get(Subscription_.emailAddress).in(c.getEmailAddresses());
			}
		};
	}
}
