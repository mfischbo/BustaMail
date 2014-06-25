package de.mfischbo.bustamail.security.specification;

import java.util.Collection;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.Actor_;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.User;

public class ActorSpecification {

	public static Specification<Actor> isActorOf(final Collection<OrgUnit> units) {
		return new Specification<Actor>() {

			@Override
			public Predicate toPredicate(Root<Actor> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return root.get(Actor_.orgUnit).in(units);
			}
		};
	}
	
	public static Specification<Actor> isAddToFutureChildSet() {
		return new Specification<Actor>() {

			@Override
			public Predicate toPredicate(Root<Actor> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.isTrue(root.get(Actor_.addToFutureChildren));
			}
		};
	}
	
	
	public static Specification<Actor> isActorOf(final OrgUnit unit) {
		return new Specification<Actor>() {

			@Override
			public Predicate toPredicate(Root<Actor> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.equal(root.get(Actor_.orgUnit), unit);
			}
		};
	}
	
	public static Specification<Actor> hasPermission(final UUID id) {
		return new Specification<Actor>() {

			@Override
			public Predicate toPredicate(Root<Actor> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.isMember(id, root.get(Actor_.permissions));
			}
		};
	}
	
	public static Specification<Actor> hasNoPermissions() {
		return new Specification<Actor>() {

			@Override
			public Predicate toPredicate(Root<Actor> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.isEmpty(root.get(Actor_.permissions));
			}
		};
	}
	
	public static Specification<Actor> isUser(final User u) {
		return new Specification<Actor>() {

			@Override
			public Predicate toPredicate(Root<Actor> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.equal(root.get(Actor_.user), u);
			}
		};
	}
}
