package de.mfischbo.bustamail.security.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailer.service.SimpleMailService;
import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.dto.ActorDTO;
import de.mfischbo.bustamail.security.dto.AuthenticationDTO;
import de.mfischbo.bustamail.security.dto.OrgUnitDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.security.dto.UserPasswordDTO;
import de.mfischbo.bustamail.security.event.OrgUnitCreatedEvent;
import de.mfischbo.bustamail.security.repository.ActorRepository;
import de.mfischbo.bustamail.security.repository.OrgUnitRepository;
import de.mfischbo.bustamail.security.repository.UserRepository;
import de.mfischbo.bustamail.security.specification.ActorSpecification;
import de.mfischbo.bustamail.security.specification.UserSpecification;

@Service
public class SecurityServiceImpl extends BaseService implements SecurityService, ApplicationEventPublisherAware {

	private static final UUID ROOT_UNIT_ID = UUID.fromString("aef3b439-3ca9-46dc-a0e0-cdb030f25a0c");

	
	@Autowired
	private AuthenticationManager	authManager;
	
	@Autowired
	private	OrgUnitRepository		orgUnitRepo;
	
	@Autowired
	private UserRepository			userRepo;
	
	@Autowired
	private ActorRepository			actorRepo;
	
	@Autowired
	private	PasswordEncoder			pwEncoder;
	
	@Autowired
	private SimpleMailService		mailService;
	
	@Autowired
	private Authentication			currentUser;
	
	private ApplicationEventPublisher	publisher;

	
	public SecurityServiceImpl() {
		SecurityModulePermissionProvider smp = new SecurityModulePermissionProvider();
		PermissionRegistry.registerPermissions(smp.getModulePermissions());
	}
	
	
	public UserDTO signIn(AuthenticationDTO dto, HttpServletRequest req) {
		try {
			Authentication auth = 
					authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(auth);
			User u = (User) auth.getPrincipal();
			return asDTO(u, UserDTO.class);
		} catch (AuthenticationException ex) {
			SecurityContextHolder.getContext().setAuthentication(null);
			throw ex;
		}
	}

	public void signOut(HttpServletRequest req, HttpServletResponse res) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(req, res, auth);
		}
	}
	
	public void recoverUserPassword(AuthenticationDTO dto) throws EntityNotFoundException {
		User u = userRepo.findByEmail(dto.getEmail());
		if (u.isLocked() || u.isDeleted())
			return;
		
		String passwd = RandomStringUtils.randomAlphanumeric(8);
		InternetAddress toAddr = null;
		InternetAddress from   = null;
		try {
			toAddr = new InternetAddress(u.getEmail());
			from   = new InternetAddress("noreply-bustamail@example.com");
		} catch (Exception ex) {
			log.error("Unable to generate InternetAddress from string " + u.getEmail());
		}
		u.setPassword(pwEncoder.encode(passwd));
		userRepo.saveAndFlush(u);
		mailService.sendSimpleMail(from, toAddr, "Your password has been reset", passwd);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getRootOrgUnit()
	 */
	@Override
	public OrgUnitDTO getRootOrgUnit() {
		return asDTO(orgUnitRepo.findOne(ROOT_UNIT_ID), OrgUnitDTO.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getRootOrgUnits()
	 */
	@Override
	public Set<OrgUnitDTO> getTopLevelUnits() {
		OrgUnit root = orgUnitRepo.findOne(ROOT_UNIT_ID);
		User current = (User) currentUser.getPrincipal();
		Set<OrgUnit> units = getOrgUnitsOfUser(current);
	
		Set<OrgUnit> retval = new LinkedHashSet<>();
		Iterator<OrgUnit> oit = root.getChildren().iterator();
		while (oit.hasNext()) {
			OrgUnit nxt = oit.next();
			if (units.contains(nxt))
				retval.add(nxt);
				
		}
		return asDTO(retval, OrgUnitDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getOrgUnitById(java.util.UUID)
	 */
	@Override
	public OrgUnit getOrgUnitById(UUID id) {
		return orgUnitRepo.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#createOrgUnit(java.util.UUID, de.mfischbo.bustamail.security.dto.OrgUnitDTO)
	 */
	@Override
	public OrgUnitDTO createOrgUnit(UUID parent, OrgUnitDTO dto) throws EntityNotFoundException {
		OrgUnit unit = new OrgUnit();
		unit.setDescription(dto.getDescription());
		unit.setName(dto.getName());
		unit.setDateCreated(DateTime.now());
		unit.setDateModified(unit.getDateCreated());
		if (parent != null) {
			OrgUnit pu = orgUnitRepo.findOne(parent);
			if (pu == null)
				throw new EntityNotFoundException("Unable to find org unit with id : " + parent);
			unit.setParent(pu);
		} else {
			// set the hidden root category as parent
			OrgUnit pu = orgUnitRepo.findOne(ROOT_UNIT_ID);
			unit.setParent(pu);
		}
		unit = orgUnitRepo.save(unit);
		
		// find all actors from parent units that have the addToFutureChildren flag enabled
		List<OrgUnit> parents = new LinkedList<OrgUnit>();
		OrgUnit leaf = unit;
		while (!leaf.getId().equals(ROOT_UNIT_ID)) {
			if (leaf.getParent() != null)
				parents.add(leaf.getParent());
			leaf = leaf.getParent();
		}
		
	
		if (parents.size() > 0) {
			Specifications<Actor> specs = 
					Specifications.where(ActorSpecification.isActorOf(parents))
					.and(ActorSpecification.isAddToFutureChildSet());
			List<Actor> actors = actorRepo.findAll(specs);
			
			// distinct set of users to prevent creating an actor twice
			Set<User> users = new HashSet<User>();
			for (Actor a : actors)
				users.add(a.getUser());
			
			for (Actor a : actors) {
				Actor na = new Actor();
				na.setAddToChildren(a.isAddToChildren());
				na.setAddToFutureChildren(true);
				na.setOrgUnit(unit);
			
				Set<UUID> perms = new HashSet<UUID>();
				perms.addAll(a.getPermissions());
				na.setPermissions(perms);
				na.setUser(a.getUser());
				
				// check if the user is in the set if so create actor and remove user
				if (users.contains(a.getUser())) {
					actorRepo.save(na);
					users.remove(a.getUser());
				}
			}
		}
		publisher.publishEvent(new OrgUnitCreatedEvent(this, unit));
		return mapper.map(unit, OrgUnitDTO.class);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#updateOrgUnit(de.mfischbo.bustamail.security.dto.OrgUnitDTO)
	 */
	@Override
	public OrgUnitDTO updateOrgUnit(@P("unit") OrgUnitDTO unit) throws EntityNotFoundException {
		OrgUnit u = orgUnitRepo.findOne(unit.getId());
		checkOnNull(u);
		u.setName(unit.getName());
		u.setDescription(unit.getDescription());
		u.setDateModified(DateTime.now());
		return asDTO(orgUnitRepo.saveAndFlush(u), OrgUnitDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#deleteOrgUnit(java.util.UUID)
	 */
	@Override
	public void deleteOrgUnit(UUID id) throws EntityNotFoundException {
		OrgUnit unit = orgUnitRepo.findOne(id);
		checkOnNull(unit);
		unit.setDeleted(true);
		orgUnitRepo.saveAndFlush(unit);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getAllUsers(org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<UserDTO> getAllUsers(Pageable page) {
		User current = (User) currentUser.getPrincipal();
		Set<OrgUnit> units = getOrgUnitsOfUser(current);
		
		Specifications<User> specs = Specifications.not(UserSpecification.isDeleted());
		specs = specs.and(UserSpecification.isMemberInOneOf(units));
		specs = specs.and(UserSpecification.distinct());
		return asDTO(userRepo.findAll(specs, page), UserDTO.class, page);
	}

	
	
	private Set<OrgUnit> getOrgUnitsOfUser(User u) {
	
		Specifications<Actor> specs = Specifications.where(ActorSpecification.isUser(u));
		Set<OrgUnit> retval = new HashSet<OrgUnit>();
		for (Actor a : actorRepo.findAll(specs))
			retval.add(a.getOrgUnit());
		return retval;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getUsersByOrgUnit(de.mfischbo.bustamail.security.domain.OrgUnit, org.springframework.data.domain.Pageable)
	 */
	@Override
	@PreAuthorize("hasPermission(#unit, 'Security.MANAGE_USER')")
	public Page<UserDTO> getUsersByOrgUnit(OrgUnit unit, Pageable page) throws EntityNotFoundException {
		Specifications<User> specs = Specifications.not(UserSpecification.isDeleted());
		specs = specs.and(UserSpecification.isActorIn(unit));
		specs = specs.and(UserSpecification.distinct());
		return asDTO(userRepo.findAll(specs, page), UserDTO.class, page);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getOrgUnitsByCurrentUser()
	 */
	public Set<OrgUnit> getOrgUnitsByCurrentUser() {
		User current = (User) currentUser.getPrincipal();
		Specifications<Actor> specs = Specifications.where(ActorSpecification.isUser(current));
		Set<OrgUnit> units = new LinkedHashSet<OrgUnit>();
		for (Actor a : actorRepo.findAll(specs)) {
			units.add(a.getOrgUnit());
		}
		return units;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#findUsers(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<UserDTO> findUsers(String searchTerm, Pageable page) {
		User current = (User) currentUser.getPrincipal();
		Set<OrgUnit> units = getOrgUnitsOfUser(current);
		
		Specifications<User> specs = Specifications.not(UserSpecification.isDeleted());
		specs = specs.and(UserSpecification.userMatches(searchTerm));
		specs = specs.and(UserSpecification.isMemberInOneOf(units));
		specs = specs.and(UserSpecification.distinct());
		return asDTO(userRepo.findAll(specs, page), UserDTO.class, page);
	}
	
	
	@Override
	public UserDTO getUserById(UUID id) throws EntityNotFoundException {
		User u = userRepo.findOne(id);
		checkOnNull(u);
		return asDTO(u, UserDTO.class);
	}

	@Override
	@Transactional
	@PreAuthorize("hasPermission(#owner, 'Security.MANAGE_USER')")
	public UserDTO createUser(@P("owner") UUID owner, UserDTO user) throws EntityNotFoundException, DataIntegrityException {
		OrgUnit ou = orgUnitRepo.findOne(owner);
		checkOnNull(ou);
		
		log.info("Creating new user with email / login : " + user.getEmail());
		
		// generate a random password
		String passwd = RandomStringUtils.randomAlphanumeric(8);
		log.debug("Setting randomized password to : "+ passwd);

		// check if email can be used
		InternetAddress iadr = null;
		try {
			iadr = new InternetAddress(user.getEmail());
		} catch (Exception ex) {
			throw new DataIntegrityException("Users email can not be recognized");
		}
		
		User u = new User();
		u.setFirstName(user.getFirstName());
		u.setLastName(user.getLastName());
		u.setEmail(user.getEmail());
		u.setPassword(pwEncoder.encode(passwd));
		u.setDateCreated(DateTime.now());
		u.setDateModified(u.getDateCreated());
		u.setGender(user.getGender());
		u = userRepo.saveAndFlush(u);

		// add the user as actor to the given org unit without any permissions
		Actor a = new Actor();
		a.setUser(u);
		a.setOrgUnit(ou);
		a.setAddToChildren(false);
		a.setAddToFutureChildren(false);
		actorRepo.saveAndFlush(a);
		
		InternetAddress from = null;
		try {
			from = new InternetAddress("noreply-bustamail@example.com");
		} catch (Exception ex) { }
		mailService.sendSimpleMail(from, iadr, "Your password", passwd);
		return asDTO(u, UserDTO.class);
	}

	@Override
	public UserDTO updateUser(UserDTO user) throws EntityNotFoundException {
		User u = userRepo.findOne(user.getId());
		checkOnNull(u);
		u.setFirstName(user.getFirstName());
		u.setLastName(user.getLastName());
		u.setDateModified(DateTime.now());
		u.setGender(user.getGender());
		return asDTO(userRepo.saveAndFlush(u), UserDTO.class);
	}
	
	@Override
	public void setUserPassword(UUID userId, UserPasswordDTO password) throws EntityNotFoundException, DataIntegrityException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		if (password.getPassword().equals(password.getPasswordConfirmation())) {
			u.setPassword(pwEncoder.encode(password.getPassword()));
			userRepo.saveAndFlush(u);
		} else {
			throw new DataIntegrityException("The provided passwords don't match");
		}
	}

	@Override
	public UserDTO lockUser(UUID userId) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		u.setLocked(true);
		return asDTO(userRepo.saveAndFlush(u), UserDTO.class);
	}

	@Override
	public UserDTO unlockUser(UUID userId) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		u.setLocked(false);
		return asDTO(userRepo.saveAndFlush(u), UserDTO.class);
	}

	@Override
	public void deleteUser(UUID userId) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		u.setDeleted(true);
		userRepo.saveAndFlush(u);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#updateActors(java.util.UUID, java.util.List)
	 */
	@Override
	public List<ActorDTO> updateActors(UUID userId, List<ActorDTO> actors) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);

		List<Actor> del = actorRepo.findByUser(u);
		actorRepo.delete(del);
		
		// explicitly created actors. This is the users selection
		List<Actor> inserts = new LinkedList<>();
		for (ActorDTO d : actors) {
			Actor a = new Actor();
			a.setAddToChildren(d.isAddToChildren());
			a.setAddToFutureChildren(d.isAddToFutureChildren());
			a.setPermissions(d.getPermissions());
			a.setUser(u);
			
			OrgUnit ou = orgUnitRepo.findOne(d.getOrgUnitId());
			checkOnNull(ou);
			a.setOrgUnit(ou);
			inserts.add(a);
		}
		List<Actor> retval = new ArrayList<>(inserts.size());
		retval = actorRepo.save(inserts);
		actorRepo.flush();
		return asDTO(retval, ActorDTO.class);
		
		/*
		// create actors for all parent units without permissions
		ListIterator<Actor> ait = inserts.listIterator();
		while (ait.hasNext()) {
			Actor t = ait.next();
		
			// don't add to the root org unit
			if (t.getOrgUnit().getParent().getId() == ROOT_UNIT_ID)
				continue;
			
			// if the user is not actor in the parent org unit add him without permissions
			if (!isActorIn(t.getOrgUnit().getParent(), inserts)) {
				Actor a2 = new Actor();
				a2.setAddToChildren(false);
				a2.setAddToFutureChildren(false);
				a2.setOrgUnit(t.getOrgUnit().getParent());
				a2.setPermissions(new LinkedHashSet<>());
				a2.setUser(u);
				ait.add(a2);
			}
			
			/*
			if (!hasParentActor(t, inserts)) {
				Actor a2 = new Actor();
				a2.setAddToChildren(false);
				a2.setAddToFutureChildren(false);
				a2.setOrgUnit(t.getOrgUnit().getParent());
				a2.setPermissions(new LinkedHashSet<UUID>());
				a2.setUser(u);
				ait.add(a2);
			}
			
		}
		*/
		
		
		/*
		// put everything into a set to make sure each actor tuple(orgunit, user)
		// is exactly created once
		Set<Actor> create = new HashSet<Actor>();
		create.addAll(inserts);
		if (log.isDebugEnabled()) {
			for (Actor a : create)
				log.debug("Adding actor : " + a.toString());
		}
	
		actorRepo.delete(u.getActors());
		u.getActors().clear();
		u.getActors().addAll(create);
		u = userRepo.saveAndFlush(u);
		return asDTO(u.getActors(), ActorDTO.class);
		*/
	}
	
	/*
	private boolean isActorIn(OrgUnit parent, List<Actor> actors) {
		for (Actor a : actors) {
			if (a.getOrgUnit().getId().equals(parent.getId()))
				return true;
		}
		return false;
	}
	
	
	private boolean hasParentActor(Actor t, List<Actor> actors) {
		UUID ouId = t.getOrgUnit().getParent().getId();
		if (ouId.equals(ROOT_UNIT_ID))
			return true;
		
		boolean found = false;
		for (Actor current : actors) {
			if (current.getOrgUnit().getId().equals(ouId))
				found = true;
		}
		return found;
	}
	*/
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher arg0) {
		this.publisher = arg0;
	}
}
