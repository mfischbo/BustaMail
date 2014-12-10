package de.mfischbo.bustamail.mailinglist.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList_;

public class SubscriptionListSpecs {

	public static Specification<SubscriptionList> matches(final String query) {
	
		return new Specification<SubscriptionList>() {

			@Override
			public Predicate toPredicate(Root<SubscriptionList> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				
				String[] terms = query.split("\\s");
				List<Predicate> ands = new ArrayList<Predicate>(terms.length);
		
				for (String t : terms) {
					if (t.length() == 0)
						continue;
					t = "%" + t + "%";
					
					Predicate p = cb.or(
							cb.like(root.get(SubscriptionList_.name), t),
							cb.like(root.get(SubscriptionList_.description), t)
					);
					ands.add(p);
				}
				return cb.and(ands.toArray(new Predicate[ands.size()]));
			}
		};
	}
}
