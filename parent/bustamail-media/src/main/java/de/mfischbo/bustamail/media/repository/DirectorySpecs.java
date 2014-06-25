package de.mfischbo.bustamail.media.repository;

import java.util.Set;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Directory_;
import de.mfischbo.bustamail.media.service.MediaServiceImpl;

public class DirectorySpecs {

	public static Specification<Directory> isChildOfGlobalRoot() {
		return new Specification<Directory>() {

			@Override
			public Predicate toPredicate(Root<Directory> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(Directory_.parent).get(Directory_.id), MediaServiceImpl.GLOBAL_ROOT_DIRECTORY_ID);
			}
		};
	}
	
	
	public static Specification<Directory> isOwnedByOneOf(final Set<UUID> units) {
		return new Specification<Directory>() {

			@Override
			public Predicate toPredicate(Root<Directory> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return root.get(Directory_.owner).in(units);
			}
		};
	}
	
	public static Specification<Directory> hasNullParent() {
		return new Specification<Directory>() {

			@Override
			public Predicate toPredicate(Root<Directory> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.isNull(root.get(Directory_.parent));
			}
		};
	}
}
