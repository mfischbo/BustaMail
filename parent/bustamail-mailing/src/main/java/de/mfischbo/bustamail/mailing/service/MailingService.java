package de.mfischbo.bustamail.mailing.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import de.mfischbo.bustamail.exception.ConfigurationException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;

public interface MailingService {

	/**
	 * Returns all mailings for the given owner
	 * @param owner The id of the owner OU owning the mailing
	 * @param page The page params
	 * @return The page of mailings 
	 */
	@PreAuthorize("hasPermission(#owner, 'Mailings.USE_MAILINGS')")
	Page<Mailing>		getAllMailings(ObjectId owner, Pageable page);


	Page<Mailing>		getAllVisibleMailings(Pageable page);
	
	/**
	 * Returns the mailing with the specified id
	 * @param id The id of the mailng to be returnd
	 * @return The mailing
	 * @throws EntityNotFoundException
	 */
	@PostAuthorize("hasPermission(returnObject.owner, 'Mailings.USE_MAILINGS')")
	Mailing				getMailingById(ObjectId id) throws EntityNotFoundException;

	/**
	 * Creates a new mailing
	 * @param m The mailing to be created
	 * @return The persisted entity of the newly created mailing
	 * @throws EntityNotFoundException
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	Mailing				createMailing(@P("m") Mailing m, Template t) throws EntityNotFoundException;
	
	/**
	 * Updates the given mailing
	 * @param m The mailing to be updated
	 * @return The persisted entity of the updated mailing
	 * @throws EntityNotFoundException
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	Mailing				updateMailing(@P("m") Mailing m) throws EntityNotFoundException;
	
	/**
	 * Deletes the mailing
	 * @param m The mailing to be deleted
	 * @throws EntityNotFoundException
	 */
	@Transactional
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				deleteMailing(@P("m") Mailing m) throws EntityNotFoundException;
	
	/**
	 * Sends a preview of the mailing to the current users mailing account
	 * @param m The mailing to be sent
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				sendPreview(@P("m") Mailing m);
	
	/**
	 * Returns a list of content versions for the specified mailing and type
	 * @param m The mailing to return the content versions for
	 * @param types The types of the content to be returned
	 * @return The list of available content versions
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	List<VersionedContent>
						getContentVersions(@P("m") Mailing m, List<ContentType> types);
	
	/**
	 * Returns the actual content for the versioned content header id
	 * @param m The mailing related to the versioned content
	 * @param contentId The id of the versioned content to be returned
	 * @return The versioned content
	 * @throws EntityNotFoundException
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.EDIT_CONTENTS')")
	VersionedContent	getContentById(@P("m") Mailing m, ObjectId contentId) throws EntityNotFoundException;
	
	/**
	 * Gets the content version with the newest dateCreated attribute
	 * @param m The mailing for which the recent content version should be returned
	 * @param type The type of the content to return 
	 * @return
	 */
	@PreAuthorize("hasPe#/dashboardrmission(#m.owner, 'Mailings.USE_MAILINGS')")
	VersionedContent	getRecentContent(@P("m") Mailing m, ContentType type);

	/**
	 * Creates a new content version for the given mailing
	 * @param m The mailing to create a new content version for
	 * @param c The content version to be created
	 * @return The persisted content version entity
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.EDIT_CONTENTS')")
	VersionedContent	createContentVersion(@P("m") Mailing m, VersionedContent c);

	
	/**
	 * Adds the given subscription list to the lists the mailing will be sent to.
	 * Implementations MUST ensure, that each list is only attached once
	 * @param m The mailing
	 * @param list The subscription list to be attached
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				attachSubscriptionList(@P("m") Mailing m, SubscriptionList list);
	
	/**
	 * Removes the subscription list from the lists the mailing will be sent to
	 * @param m The mailing 
	 * @param list The list
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				removeSubscriptionList(@P("m") Mailing m, SubscriptionList list);
	
	
	/**
	 * Requests the mailing to be approved by a user that has the permission to do so
	 * @param m The mailing the approval is requested for
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				requestApproval(@P("m") Mailing m);
	
	/**
	 * Denies the approval of the specified mailing
	 * @param m The mailing to be approved
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.APPROVE_MAILINGS')")
	void				denyApproval(@P("m") Mailing m);
	
	/**
	 * Approves the mailing to be published 
	 * @param m The mailing to be approved
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.APPROVE_MAILINGS')")
	void				approveMailing(@P("m") Mailing m);
	
	/**
	 * Publishes the mailing to the subscription lists that are assigned to it
	 * @param m The mailing to be published
	 */
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.PUBLISH_MAILINGS')")
	boolean				publishMailing(@P("m") Mailing m) throws ConfigurationException;

	/**
	 * Checks if the given hyperlink would return a successful HTTP code
	 * @param dtos
	 * @return
	 */
	List<HyperlinkDTO>  checkHyperlinkConnectivity(List<HyperlinkDTO> dtos);
}
