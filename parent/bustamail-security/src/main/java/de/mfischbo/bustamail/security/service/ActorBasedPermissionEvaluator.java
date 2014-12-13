package de.mfischbo.bustamail.security.service;

import java.io.Serializable;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.domain.User;

public class ActorBasedPermissionEvaluator implements PermissionEvaluator {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	//@Inject
	//private OrgUnitRepository	orgUnitRepo;

	@Inject
	private MongoTemplate		mTemp;
	
	@Inject
	private PermissionRegistry	registry;
	
	@Override
	public boolean hasPermission(Authentication arg0, Object arg1, Object arg2) {

		OrgUnit owner = null;
		
		if (arg1 instanceof OrgUnit) 
			owner = ((OrgUnit) arg1);

		if (arg1 instanceof OwnedBaseDomain) {
			ObjectId orgUnitId = ((OwnedBaseDomain) arg1).getOwner();
			owner = mTemp.findById(orgUnitId, OrgUnit.class);
			//owner = orgUnitRepo.findOne(orgUnitId);
		}

		if (arg1 instanceof ObjectId) {
			ObjectId orgUnitId = (ObjectId) arg1;
			//owner = orgUnitRepo.findOne(orgUnitId);
			owner = mTemp.findById(orgUnitId, OrgUnit.class);
		}
		
		User u = (User) arg0.getPrincipal();
		
		String p = (String) arg2;
		if (p.equals("Security.IS_ACTOR_OF")) {
			return isMemberOf(owner, u);
		}
		
		Permission perm = registry.getPermissionByIdentificator((String) arg2);
		if (owner != null && perm != null && u != null) {
			log.debug("Checking permission " + perm.getIdentificator() + " on user : " + u.getEmail() + " with expected membership in OrgUnit " + owner.getName());;

			Actor a = null;
			for (Actor actor : owner.getActors()) {
				if (actor.getUser().equals(u)) 
					a = actor;
			}
			
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
		return false;
	}
	
	public boolean isMemberOf(OrgUnit ou, User u) {
		for (Actor a : ou.getActors()) {
			if (a.getUser().equals(u)) return true;
		}
		return false;
	}
}
