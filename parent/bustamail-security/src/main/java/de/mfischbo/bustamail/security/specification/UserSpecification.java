package de.mfischbo.bustamail.security.specification;

import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.Actor_;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.domain.User_;

public class UserSpecification {

	public static Specification<User> isDeleted() {
		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.isTrue(root.get(User_.deleted));
			}
		};
	}
	
	public static Specification<User> userMatches(final String name) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				String token = "%" + name + "%";
				return cb.or(
						cb.like(root.get(User_.firstName), 	token),
						cb.like(root.get(User_.lastName),  	token),
						cb.like(root.get(User_.email), 		token)
					);
			}
		};
	}
	
	public static Specification<User> emailIsLike(final String email) {
		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				return cb.like(root.get(User_.email), "%" + email + "%");
			}
		};
	}
	
	public static Specification<User> isActorIn(final OrgUnit unit) {
		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				Join<User, Actor> ua = root.join(User_.actors);
				return cb.equal(ua.get(Actor_.orgUnit), unit);
			}
		};
	}
	
	public static Specification<User> isMemberInOneOf(final Set<OrgUnit> units) {
		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root,
					CriteriaQuery<?> arg1, CriteriaBuilder cb) {
				Join<User, Actor> ua = root.join(User_.actors);
				return ua.get(Actor_.orgUnit).in(units); 
			}
		};
	}
	
	public static Specification<User> distinct() {
		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root,
					CriteriaQuery<?> cq, CriteriaBuilder cb) {
				cq.distinct(true);
				return cb.isNotNull(root.get(User_.id));
			}
		};
	}
}
