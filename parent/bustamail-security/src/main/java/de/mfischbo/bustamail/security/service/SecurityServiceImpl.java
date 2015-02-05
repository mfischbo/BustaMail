package de.mfischbo.bustamail.security.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailer.service.SimpleMailService;
import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.dto.ActorDTO;
import de.mfischbo.bustamail.security.dto.AuthenticationDTO;
import de.mfischbo.bustamail.security.dto.OrgUnitDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.security.dto.UserPasswordDTO;
import de.mfischbo.bustamail.security.event.OrgUnitCreatedEvent;
import de.mfischbo.bustamail.security.repository.OrgUnitRepository;
import de.mfischbo.bustamail.security.repository.UserRepository;

@Service
public class SecurityServiceImpl extends BaseService implements SecurityService, ApplicationEventPublisherAware {

	public static final ObjectId ROOT_UNIT_ID = new ObjectId("507f191e810c19729de860ea");
	
	@Autowired
	private AuthenticationManager	authManager;
	
	@Autowired
	private	OrgUnitRepository		orgUnitRepo;
	
	@Autowired
	private UserRepository			userRepo;
	
	@Autowired
	private	PasswordEncoder			pwEncoder;
	
	@Autowired
	private SimpleMailService		mailService;
	
	@Autowired
	private Authentication			currentUser;
	
	private ApplicationEventPublisher	publisher;

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#signIn(de.mfischbo.bustamail.security.dto.AuthenticationDTO, javax.servlet.http.HttpServletRequest)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#signOut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void signOut(HttpServletRequest req, HttpServletResponse res) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(req, res, auth);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#recoverUserPassword(de.mfischbo.bustamail.security.dto.AuthenticationDTO)
	 */
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
		userRepo.save(u);
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
	
		User current = (User) currentUser.getPrincipal();
		List<OrgUnit> units = orgUnitRepo.findAllWithUserAsActor(current.getId());
		
		Set<OrgUnitDTO> roots = new HashSet<>();
		
		// find all roots for the current user
		for (OrgUnit unit : units) {
			
			boolean isRoot = true;
			for (OrgUnit inner : units) {
				if (unit.getParent() != null && inner.getId().equals(unit.getParent().getId())) {
					isRoot = false;
					break;
				}
			}
			
			if (isRoot)
				roots.add(asDTO(unit, OrgUnitDTO.class));
		}
		
		// build the tree
		for (OrgUnitDTO unit : roots) {
			buildTree(unit, units);
		}
		
		return roots;
	}
	
	private OrgUnitDTO buildTree(OrgUnitDTO current, List<OrgUnit> units) {
		
		if (units.size() == 0)
			return current;
		
		Iterator<OrgUnit> uit = units.iterator();
		while (uit.hasNext()) {
			OrgUnit u = uit.next();
			if (u.getParent() != null && u.getParent().getId().equals(current.getId())) {
				OrgUnitDTO child = asDTO(u, OrgUnitDTO.class);
				current.getChildren().add(child);
				uit.remove();
				return buildTree(child, units);
			}
		}
		return current;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getOrgUnitById(java.util.UUID)
	 */
	@Override
	public OrgUnit getOrgUnitById(ObjectId id) {
		return orgUnitRepo.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#createOrgUnit(java.util.UUID, de.mfischbo.bustamail.security.dto.OrgUnitDTO)
	 */
	@Override
	public OrgUnitDTO createOrgUnit(ObjectId parent, OrgUnitDTO dto) throws EntityNotFoundException {
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
		
		
		// find all actors from parent units that have the addToFutureChildren flag enabled
		Set<OrgUnit> parents = getParentUnits(unit);
		final Set<Actor> nas = new HashSet<>();
		if (parents.size() > 0) {
			
			for (OrgUnit p : parents) {
				p.getActors().forEach(new Consumer<Actor>() {

					@Override
					public void accept(Actor t) {
						if (t.isAddToFutureChildren()) {
							Actor na = new Actor();
							na.setAddToChildren(t.isAddToChildren());
							na.setAddToFutureChildren(t.isAddToFutureChildren());
							na.setPermissions(t.getPermissions());
							na.setUser(t.getUser());
							nas.add(na);
						}
					}
				});
			}
		}
		unit.setActors(nas);
		unit = orgUnitRepo.save(unit);
		publisher.publishEvent(new OrgUnitCreatedEvent(this, unit));
		return mapper.map(unit, OrgUnitDTO.class);
	}
	
	private Set<OrgUnit> getParentUnits(OrgUnit unit) {
		Set<OrgUnit> parents = new HashSet<>();
		OrgUnit leaf = unit;
		while (!ROOT_UNIT_ID.equals(leaf.getId())) {
			if (leaf.getParent() != null)
				parents.add(leaf.getParent());
			leaf = leaf.getParent();
		}
		return parents;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#updateOrgUnit(de.mfischbo.bustamail.security.dto.OrgUnitDTO)
	 */
	@Override
	public OrgUnitDTO updateOrgUnit(OrgUnitDTO unit) throws EntityNotFoundException {
		OrgUnit u = orgUnitRepo.findOne(unit.getId());
		checkOnNull(u);
		u.setName(unit.getName());
		u.setDescription(unit.getDescription());
		u.setDateModified(DateTime.now());
		return asDTO(orgUnitRepo.save(u), OrgUnitDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#deleteOrgUnit(java.util.UUID)
	 */
	@Override
	public void deleteOrgUnit(ObjectId id) throws EntityNotFoundException {
		OrgUnit unit = orgUnitRepo.findOne(id);
		checkOnNull(unit);
		unit.setDeleted(true);
		orgUnitRepo.save(unit);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getAllUsers(org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<UserDTO> getAllUsers(Pageable page) {
		User current = (User) currentUser.getPrincipal();
		Set<OrgUnit> units = getOrgUnitsOfUser(current);
		Set<ObjectId> userIds = new HashSet<>();
		for (OrgUnit ou : units) {
			ou.getActors().forEach( p -> userIds.add(p.getUser().getId()) );
		}
		
		return asDTO(userRepo.findAllUsers(userIds, page), UserDTO.class, page);
	}

	
	
	private Set<OrgUnit> getOrgUnitsOfUser(User u) {
		List<OrgUnit> units = orgUnitRepo.findAllWithUserAsActor(u.getId());
		Set<OrgUnit> retval = new HashSet<>();
		retval.addAll(units);
		return retval;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getUsersByOrgUnit(de.mfischbo.bustamail.security.domain.OrgUnit, org.springframework.data.domain.Pageable)
	 */
	@Override
	@PreAuthorize("hasPermission(#unit, 'Security.MANAGE_USER')")
	public Page<UserDTO> getUsersByOrgUnit(OrgUnit unit, Pageable page) throws EntityNotFoundException {
		Set<ObjectId> userIds = new HashSet<>();
		unit.getActors().forEach( p -> userIds.add(p.getUser().getId() ) );
		return asDTO(userRepo.findAllUsers(userIds, page), UserDTO.class, page);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#getOrgUnitsByCurrentUser()
	 */
	@Override
	public Set<OrgUnit> getOrgUnitsByCurrentUser() {
		User current = (User) currentUser.getPrincipal();
		List<OrgUnit> units = orgUnitRepo.findAllWithUserAsActor(current.getId());
		Set<OrgUnit> retval = new HashSet<>();
		retval.addAll(units);
		return retval;
	}
	
	
	@Override
	public Set<OrgUnit>		getOrgUnitsByCurrentUserWithPermissions(Set<Permission> permissions) {
		User current = (User) currentUser.getPrincipal();
		List<UUID> permIds = new ArrayList<>(permissions.size());
		permissions.forEach(p -> permIds.add(p.getId()));
	
		List<OrgUnit> units = orgUnitRepo.findAllWithUserAsActorAndAllPermissions(current.getId(), permIds);
		Set<OrgUnit> retval = new HashSet<>();
		units.forEach( u -> retval.add(u) );
		return retval;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#findUsers(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<UserDTO> findUsers(String searchTerm, Pageable page) {

		// TODO: Elastic?
		return new PageImpl<UserDTO>(new ArrayList<UserDTO>(0));
	}
	
	
	@Override
	public UserDTO getUserById(ObjectId id) throws EntityNotFoundException {
		User u = userRepo.findOne(id);
		checkOnNull(u);
		return asDTO(u, UserDTO.class);
	}

	@Override
	@Transactional
	@PreAuthorize("hasPermission(#owner, 'Security.MANAGE_USER')")
	public UserDTO createUser(ObjectId owner, UserDTO user) throws EntityNotFoundException, DataIntegrityException {
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
		u = userRepo.save(u);

		// add the user as actor to the given org unit without any permissions
		Actor a = new Actor();
		a.setUser(u);
		a.setAddToChildren(false);
		a.setAddToFutureChildren(false);
		ou.getActors().add(a);
		orgUnitRepo.save(ou);
		
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
		return asDTO(userRepo.save(u), UserDTO.class);
	}
	
	@Override
	public void setUserPassword(ObjectId userId, UserPasswordDTO password) throws EntityNotFoundException, DataIntegrityException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		if (password.getPassword().equals(password.getPasswordConfirmation())) {
			u.setPassword(pwEncoder.encode(password.getPassword()));
			userRepo.save(u);
		} else {
			throw new DataIntegrityException("The provided passwords don't match");
		}
	}

	@Override
	public UserDTO lockUser(ObjectId userId) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		u.setLocked(true);
		return asDTO(userRepo.save(u), UserDTO.class);
	}

	@Override
	public UserDTO unlockUser(ObjectId userId) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		u.setLocked(false);
		return asDTO(userRepo.save(u), UserDTO.class);
	}

	@Override
	public void deleteUser(ObjectId userId) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);
		u.setDeleted(true);
		userRepo.save(u);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.security.service.SecurityService#updateActors(java.util.UUID, java.util.List)
	 */
	@Override
	public List<ActorDTO> updateActors(ObjectId userId, List<ActorDTO> actors) throws EntityNotFoundException {
		User u = userRepo.findOne(userId);
		checkOnNull(u);

		List<OrgUnit> units = orgUnitRepo.findAllWithUserAsActor(u.getId());
		for (OrgUnit ou : units) {
			ou.getActors().removeIf( p -> p.getUser().equals(u) );
			orgUnitRepo.save(ou);
		}
		
		// explicitly created actors. This is the users selection
		List<Actor> inserts = new LinkedList<>();
		for (ActorDTO d : actors) {
			Actor a = new Actor();
			a.setAddToChildren(d.isAddToChildren());
			a.setAddToFutureChildren(d.isAddToFutureChildren());
			a.setPermissions(d.getPermissions());
			a.setUser(u);
			
			OrgUnit ou = orgUnitRepo.findOne(d.getOrgUnitId());
			ou.getActors().add(a);
			orgUnitRepo.save(ou);
			inserts.add(a);
		}
		List<Actor> retval = new ArrayList<>(inserts.size());
		return asDTO(retval, ActorDTO.class);
	}
	
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher arg0) {
		this.publisher = arg0;
	}
}
