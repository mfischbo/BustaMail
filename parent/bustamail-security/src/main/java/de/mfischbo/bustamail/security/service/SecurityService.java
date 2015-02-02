package de.mfischbo.bustamail.security.service;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.dto.ActorDTO;
import de.mfischbo.bustamail.security.dto.AuthenticationDTO;
import de.mfischbo.bustamail.security.dto.OrgUnitDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.security.dto.UserPasswordDTO;

public interface SecurityService {

	/**
	 * Tries to log in the user
	 * @param dto The DTO providing the credentials
	 * @param req The HttpRequest
	 * @return The UserDTO if login was successful
	 */
	public UserDTO signIn(AuthenticationDTO dto, HttpServletRequest req);

	/**
	 * Logs out the user and destroys his current authentication
	 * @param req The HttpRequest
	 * @param res The HttpResponse
	 */
	public void signOut(HttpServletRequest req, HttpServletResponse res);
	
	
	/**
	 * Sends a new password to the users email address
	 * @param dto The DTO containing only the email address of the user
	 * @throws EntityNotFoundException If no such user account exists
	 */
	public void recoverUserPassword(AuthenticationDTO dto) throws EntityNotFoundException;

	
	/**
	 * Returns the current users org units that are children of the root unit.
	 * Each user is added to it's parent's units by default without any permissions.
	 * Permissions must be granted on each unit individually.
	 * Hence the user is always a actor in the org units that are children of the
	 * root org unit. Those are returned here
	 * @return
	 */
	public Set<OrgUnitDTO>	getTopLevelUnits();
	
	/**
	 * Returns the upmost org unit of the hierarchy. 
	 * @return The root unit 
	 */
	@PostAuthorize("hasPermission(returnObject.id, 'Security.MANAGE_ORG_UNITS')")
	public OrgUnitDTO		getRootOrgUnit();
	
	/**
	 * Returns the org unit with the specified id
	 * @param id The id of the unit to be returned
	 * @return The unit
	 * @throws EntityNotFoundException If no unit with that id exists
	 */
	@PreAuthorize("hasPermission(#id, 'Security.IS_ACTOR_OF')")
	public OrgUnit			getOrgUnitById(ObjectId id) throws EntityNotFoundException;


	/**
	 * Returns the org units the current user is member of.
	 * @return
	 */
	public Set<OrgUnit>		getOrgUnitsByCurrentUser();
	
	/**
	 * Creates a new org unit
	 * @param parent The id of the parent unit the created unit will be child of
	 * @param dto The DTO containing the data for the unit
	 * @return The created unit
	 * @throws EntityNotFoundException If no unit for the given parent id exists
	 */
	@PreAuthorize("hasPermission(#parent, 'Security.MANAGE_ORG_UNITS')")
	public OrgUnitDTO		createOrgUnit(ObjectId parent, OrgUnitDTO dto) throws EntityNotFoundException;
	
	/**
	 * Updates the given org unit
	 * @param unit The DTO containing the data of the unit
	 * @return The updated unit
	 * @throws EntityNotFoundException If no unit for the given id exists
	 */
	@PreAuthorize("hasPermission(#unit, 'Security.MANAGE_ORG_UNITS')")
	public OrgUnitDTO		updateOrgUnit(OrgUnitDTO unit) throws EntityNotFoundException;
	
	/**
	 * Deletes the unit
	 * @param id The id of the unit to be marked as deleted
	 * @throws EntityNotFoundException If no unit for the given id exists
	 */
	@PreAuthorize("hasPermission(#id, 'Security.MANAGE_ORG_UNITS')")
	public void				deleteOrgUnit(ObjectId id) throws EntityNotFoundException;
	
	
	// ----------------------------------------- //
	// 		User Management						//
	// ----------------------------------------//
	
	public Page<UserDTO> 	getAllUsers(Pageable page);
	public Page<UserDTO>	getUsersByOrgUnit(OrgUnit unit, Pageable page) throws EntityNotFoundException;
	public Page<UserDTO>	findUsers(String searchTerm, Pageable page);
	public UserDTO			getUserById(ObjectId id) throws EntityNotFoundException;
	
	
	public UserDTO			createUser(ObjectId owner, UserDTO user) throws EntityNotFoundException, DataIntegrityException;
	
	public UserDTO			updateUser(UserDTO user) throws EntityNotFoundException;
	public void				setUserPassword(ObjectId userId, UserPasswordDTO dto) throws EntityNotFoundException, DataIntegrityException;
	public UserDTO			lockUser(ObjectId userId) throws EntityNotFoundException;
	public UserDTO			unlockUser(ObjectId userId) throws EntityNotFoundException;
	public void				deleteUser(ObjectId userId) throws EntityNotFoundException;
	
	
	// ----------------------------------------- //
	// 		User Permission Management			//
	// ----------------------------------------//

	/**
	 * Sets all actors for the current user
	 * @param userId The id of the user to update the actors for
	 * @param actors The list of actors the user will represent
	 * @return
	 * @throws EntityNotFoundException
	 */
	public List<ActorDTO>	updateActors(ObjectId userId, List<ActorDTO> actors) throws EntityNotFoundException;
	
}
