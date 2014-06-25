package de.mfischbo.bustamail.security.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.OrgUnit_;

public class OrgUnitSpecification {

	public static Specification<OrgUnit> isRootOrgUnit() {
		return new Specification<OrgUnit>() {

			@Override
			public Predicate toPredicate(Root<OrgUnit> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.isNull(root.get(OrgUnit_.parent));
			}
		};
	}
	
	public static Specification<OrgUnit> isDeleted() {
		return new Specification<OrgUnit>() {

			@Override
			public Predicate toPredicate(Root<OrgUnit> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.isTrue(root.get(OrgUnit_.deleted));
			}
		};
	}
}
