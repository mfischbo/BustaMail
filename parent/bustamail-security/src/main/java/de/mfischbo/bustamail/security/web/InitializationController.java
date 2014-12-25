package de.mfischbo.bustamail.security.web;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.domain.Gender;
import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.event.OrgUnitCreatedEvent;
import de.mfischbo.bustamail.security.repository.OrgUnitRepository;
import de.mfischbo.bustamail.security.repository.UserRepository;
import de.mfischbo.bustamail.security.service.PermissionProvider;
import de.mfischbo.bustamail.security.service.SecurityServiceImpl;

@RestController
@RequestMapping("/api/setup")
public class InitializationController {

	@Inject
	private OrgUnitRepository	ouRepo;

	@Inject
	private UserRepository		uRepo;

	@Inject
	private List<PermissionProvider>	permProviders;	
	
	@Inject
	private ApplicationEventPublisher	publisher;
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public void setup() {
	
		// create a user
		User u = new User();
		u.setDateCreated(DateTime.now());
		u.setDateModified(u.getDateCreated());
		u.setDeleted(false);
		u.setEmail("schdahle@art-ignition.de");
		u.setFirstName("John");
		u.setLastName("Doe");
		u.setGender(Gender.N);
		u.setHidden(false);
		u.setLocked(false);
		u.setPassword("$2a$04$zfmWwsUXZfRZqTYtoFBjmOx2aIyKv7Gqa1Q1aesD221yn62tcUGQG");
		u = uRepo.save(u);

		// create the root org unit
		OrgUnit root = new OrgUnit();
		root.setId(SecurityServiceImpl.ROOT_UNIT_ID);
		root.setDateCreated(DateTime.now());
		root.setDateModified(DateTime.now());
		root.setDeleted(false);
		root.setDescription("Root organizational unit");
		root.setName("ROOT OU");
		root.setParent(null);
		
		Actor a = new Actor();
		a.setAddToChildren(true);
		a.setAddToFutureChildren(true);
		a.setUser(u);
	
		Set<UUID> perms = new HashSet<>();
		for (PermissionProvider pp : permProviders) {
			pp.getModulePermissions().forEach( p -> perms.add(p.getId() ));
		}
		a.setPermissions(perms);
		Set<Actor> rootActors = new HashSet<>();
		rootActors.add(a);
		root.setActors(rootActors);
		root = ouRepo.save(root);
		
		OrgUnit ou = new OrgUnit();
		ou.setDateCreated(DateTime.now());
		ou.setDateModified(ou.getDateCreated());
		ou.setDeleted(false);
		ou.setDescription("First real unit");
		ou.setName("Test Unit");
		ou.setParent(root);
		ou.setActors(rootActors);
		ou = ouRepo.save(ou);
	
		publisher.publishEvent(new OrgUnitCreatedEvent(this, root));
		publisher.publishEvent(new OrgUnitCreatedEvent(this, ou));
	}
}
