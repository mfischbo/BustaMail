package de.mfischbo.bustamail.landingpage.service;

import java.io.OutputStream;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.HTMLPage;
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.vc.domain.VersionedContent;

public interface LandingPageService {

	/**
	 * Returns a page of landing pages for the given owner
	 * @param owner The owner id
	 * @param page The page parameters
	 * @return The page of landing pages
	 */
	Page<LandingPage>	getLandingPagesByOwner(ObjectId owner, Pageable page);
	
	/**
	 * Returns the landing page with the specified id
	 * @param id The id of the landing page
	 * @return The landing page
	 * @throws EntityNotFoundException If no such page exists
	 */
	LandingPage			getLandingPageById(ObjectId id) throws EntityNotFoundException;

	/**
	 * Creates a new landing page
	 * @param page The page to be created
	 * @return The persisted instance of the page
	 * @throws EntityNotFoundException 
	 */
	LandingPage			createLandingPage(LandingPage page) throws EntityNotFoundException;
	
	/**
	 * Updates the given landing page
	 * @param page The page to be updated
	 * @return The persisted instance
	 * @throws EntityNotFoundException
	 * @throws DataIntegrityException
	 */
	LandingPage			updateLandingPage(LandingPage page) throws EntityNotFoundException, DataIntegrityException;
	
	/**
	 * Deletes the landing page
	 * @param page The page to be deleted
	 * @throws DataIntegrityException
	 */
	void				deleteLandingPage(LandingPage page) throws DataIntegrityException; 
	
	/**
	 * Publishes a preview of the landing page
	 * @param page The page to be published
	 */
	void				publishPreview(LandingPage page);
	
	/**
	 * Publishes the landing page in live mode
	 * @param page The page to be published
	 * @return
	 */
	LandingPage			publishLive(LandingPage page);
	
	/**
	 * Unpublishes a page that has been published in live mode
	 * @param page The page to be unpublished 
	 * @return 
	 * @throws BustaMailException
	 */
	LandingPage			unpublishLive(LandingPage page) throws BustaMailException;
	
	/**
	 * Exports a landing page as zipfile and writes it to the given output stream
	 * @param page The page to be exported
	 * @param stream The stream to write the zip file to
	 * @throws BustaMailException
	 */
	void				exportLandingPage(LandingPage page, OutputStream stream) throws BustaMailException;

	
	/**
	 * Returns all versioned content entities for the given page
	 * @param page The page
	 * @return The List of versioned content entities
	 */
	List<VersionedContent>
						getContentVersions(HTMLPage page);
	
	/**
	 * Returns the versioned content for the given id
	 * @param page The page holding the versioned content
	 * @param contentId The id of the content
	 * @return The versioned content
	 * @throws EntityNotFoundException if no such entity exists
	 */
	VersionedContent	getContentVersionById(HTMLPage page, ObjectId contentId) throws EntityNotFoundException;
	
	/**
	 * Returns the most recent version for the given page
	 * @param page The page
	 * @return
	 */
	VersionedContent	getRecentContentVersionByPage(HTMLPage page);
	
	/**
	 * Creates a new content version for the given page
	 * @param page The page
	 * @param content The content
	 * @return The persisted entity
	 */
	VersionedContent	createContentVersion(HTMLPage page, VersionedContent content);

	/**
	 * Returns the static page for the given id
	 * @param id The id of the static page
	 * @return
	 * @throws EntityNotFoundException
	 */
	StaticPage			getStaticPageById(ObjectId id) throws EntityNotFoundException;
	
	/**
	 * Creates a new static page
	 * @param parent The landing page containing the static page
	 * @param staticPage The static page
	 * @return
	 * @throws EntityNotFoundException
	 */
	StaticPage			createStaticPage(LandingPage parent, StaticPage staticPage) throws EntityNotFoundException;
	
	/**
	 * Updates the given static page
	 * @param staticPage The static page
	 * @return The updated instance
	 * @throws EntityNotFoundException
	 */
	StaticPage			updateStaticPage(StaticPage staticPage) throws EntityNotFoundException;
	
	/**
	 * Deletes the given static page
	 * @param page The static page to be deleted
	 */
	void				deleteStaticPage(StaticPage page);

	/**
	 * Returns the form for the given id
	 * @param id The id of the form
	 * @return The form
	 * @throws EntityNotFoundException
	 */
	LPForm				getFormById(ObjectId id) throws EntityNotFoundException;
	
	/**
	 * Creates a new form for the given landing page
	 * @param page The page containing the form
	 * @param form The form to be created
	 * @return The persisted instance of the form
	 * @throws EntityNotFoundException
	 */
	LPForm				createForm(LandingPage page, LPForm form) throws EntityNotFoundException;
	
	/**
	 * Updates the given form
	 * @param form The form to be updated
	 * @return
	 * @throws EntityNotFoundException
	 */
	LPForm				updateForm(LPForm form) throws EntityNotFoundException;
	void				deleteForm(LPForm form);
	
	LPFormSubmission	createFormSubmission(LPFormSubmission submission);
}