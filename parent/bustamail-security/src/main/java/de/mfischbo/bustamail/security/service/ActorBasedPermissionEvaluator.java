package de.mfischbo.bustamail.security.service;

import java.io.Serializable;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.repository.ActorRepository;
import de.mfischbo.bustamail.security.repository.OrgUnitRepository;
import de.mfischbo.bustamail.security.specification.ActorSpecification;

public class ActorBasedPermissionEvaluator implements PermissionEvaluator {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	private ActorRepository		actorRepo;
	
	@Inject
	private OrgUnitRepository	orgUnitRepo;

	@Inject
	private PermissionRegistry	registry;
	
	@Override
	public boolean hasPermission(Authentication arg0, Object arg1, Object arg2) {

		OrgUnit owner = null;
		
		if (arg1 instanceof OrgUnit) 
			owner = ((OrgUnit) arg1);

		if (arg1 instanceof OwnedBaseDomain) {
			UUID orgUnitId = ((OwnedBaseDomain) arg1).getOwner();
			owner = orgUnitRepo.findOne(orgUnitId);
		}

		if (arg1 instanceof UUID) {
			UUID orgUnitId = (UUID) arg1;
			owner = orgUnitRepo.findOne(orgUnitId);
		}
		
		User u = (User) arg0.getPrincipal();
		
		String p = (String) arg2;
		if (p.equals("Security.IS_ACTOR_OF")) {
			return isMemberOf(owner, u);
		}
		
		Permission perm = registry.getPermissionByIdentificator((String) arg2);
		if (owner != null && perm != null && u != null) {
			log.debug("Checking permission " + perm.getIdentificator() + " on user : " + u.getEmail() + " with expected membership in OrgUnit " + owner.getName());;
			Specifications<Actor> specs = 
					Specifications.where(ActorSpecification.isActorOf(owner))
						.and(ActorSpecification.isUser(u));
						//.and(ActorSpecification.hasPermission(perm.getId()));
			
			Actor a = actorRepo.findOne(specs);
			if (a == null) {
				log.debug("Permission denied");
				return false;	
			}
			if (a.getPermissions() != null) {
				boolean retval = (a.getPermissions().contains(perm.getId()));
				log.debug("Permission " + (retval?"granted" : "denied"));
				return retval;
			}
		}
		log.debug("Permission denied!");
		return false;
	}

	@Override
	public boolean hasPermission(Authentication arg0, Serializable arg1,
			String arg2, Object arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isMemberOf(OrgUnit ou, User u) {
		
		// check if the user is actor in the given ou
		Specifications<Actor> specs = Specifications.where(ActorSpecification.isActorOf(ou))
				.and(ActorSpecification.isUser(u));
		return (actorRepo.count(specs) > 0);
	}
}
