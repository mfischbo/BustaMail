package de.mfischbo.bustamail.mailing.repository;

import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.mailing.domain.VersionedContent;
import de.mfischbo.bustamail.mailing.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.mailing.domain.VersionedContent_;


public class VersionedContentSpecification {

	public static Specification<VersionedContent> mailingIdIs(final UUID id) {
		return new Specification<VersionedContent>() {

			@Override
			public Predicate toPredicate(Root<VersionedContent> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(VersionedContent_.mailingId), id);
			}
		};
	}
	
	public static Specification<VersionedContent> typeIs(final ContentType type) {
		return new Specification<VersionedContent>() {

			@Override
			public Predicate toPredicate(Root<VersionedContent> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(VersionedContent_.type), type);
			}
		};
	}
	
	public static Specification<VersionedContent> withAnyTypeOf(final List<ContentType> types) {
		return new Specification<VersionedContent>() {

			@Override
			public Predicate toPredicate(Root<VersionedContent> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return root.get(VersionedContent_.type).in(types);
			}
		};
	}
}
