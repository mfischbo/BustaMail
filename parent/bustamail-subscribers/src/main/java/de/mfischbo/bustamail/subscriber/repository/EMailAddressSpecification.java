package de.mfischbo.bustamail.subscriber.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress_;

public class EMailAddressSpecification {

	public static Specification<EMailAddress> isEqualTo(final EMailAddress other) {
		return new Specification<EMailAddress>() {

			@Override
			public Predicate toPredicate(Root<EMailAddress> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(
						cb.equal(root.get(EMailAddress_.localPart), other.getLocalPart()),
						cb.equal(root.get(EMailAddress_.domainPart), other.getDomainPart())
				);
			}
		};
	}
}
