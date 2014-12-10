package de.mfischbo.bustamail.mailinglist.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.domain.Subscription_;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.Contact_;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress_;

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
	
	public static Specification<Subscription> withState(final State state) {
		return new Specification<Subscription>() {

			@Override
			public Predicate toPredicate(Root<Subscription> root,
					CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.equal(root.get(Subscription_.state), state);
			}
		};
	}
	
	public static Specification<Subscription> withContactMatching(final String query) {
		return new Specification<Subscription>() {

			@Override
			public Predicate toPredicate(Root<Subscription> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
		
				Join<Subscription, Contact> cj = root.join(Subscription_.contact);
				Join<Contact, EMailAddress> ca = cj.join(Contact_.emailAddresses);
				
				String[] terms = query.split("\\s");
				List<Predicate> ands = new ArrayList<Predicate>(terms.length);
				
				for (String t : terms) {
					if (t.length() == 0)
						continue;
					
					t = "%" + t + "%";
					Predicate p = cb.or(
							cb.like(cj.get(Contact_.firstName), t),
							cb.like(cj.get(Contact_.lastName), 	t),
							cb.like(ca.get(EMailAddress_.domainPart), t),
							cb.like(ca.get(EMailAddress_.localPart), t)
					);
					ands.add(p);
				}
				return cb.and(ands.toArray(new Predicate[ands.size()]));
			}
		};
	}
}
