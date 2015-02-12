package de.mfischbo.bustamail.landingpage.web;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;
import de.mfischbo.bustamail.vc.dto.VersionedContentIndexDTO;

/**
 * RESTful controller to manage landing pages
 * @author M. Fischboeck
 *
 */
@RestController
@RequestMapping("/api/landingpages")
public class RestLandingPageController extends BaseApiController {

	@Inject
	private LandingPageService		service;

	/**
	 * Returns a page of landing pages
	 * @param owner The owner of the landing pages
	 * @param page The page parameters
	 * @return 
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<LandingPage> getAllLandingPages(@RequestParam("owner") ObjectId owner, 
			@PageableDefault(size=30, value=0) Pageable page) {
		return service.getLandingPagesByOwner(owner, page);
	}

	/**
	 * Returns the landing page by the specified id
	 * @param id The id of the landing page
	 * @return The landing page
	 * @throws EntityNotFoundException If no such landing page exists
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public LandingPage getLandingPageById(@PathVariable("id") ObjectId id) throws EntityNotFoundException {
		LandingPage page = service.getLandingPageById(id);
		return page;
	}
	
	@RequestMapping(value = "/{id}/export", method = RequestMethod.GET)
	public void exportLandingPage(@PathVariable("id") ObjectId id, HttpServletResponse response) throws BustaMailException {
	
		LandingPage p = service.getLandingPageById(id);
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + p.getName() + ".zip");
		response.setHeader("Content-Transfer-Encoding", "binary");
		try {
			service.exportLandingPage(p, response.getOutputStream());
		} catch (Exception ex) {
			log.error("Unable to export landing page. Cause: " + ex.getMessage());
			ex.printStackTrace();
			throw new BustaMailException(ex.getMessage());
		}
	}
	
	/**
	 * Creates a new landing page
	 * @param page The page to be created
	 * @return
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public LandingPage createLandingPage(@RequestBody LandingPage page) throws EntityNotFoundException {
		return service.createLandingPage(page);
	}

	/**
	 * Updates the given landing page
	 * @param page The page 
	 * @return The persisted entity
	 * @throws EntityNotFoundException
	 * @throws DataIntegrityException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public LandingPage updateLandingPage(@RequestBody LandingPage page) throws EntityNotFoundException, DataIntegrityException {
		return service.updateLandingPage(page);
	}

	
	/**
	 * Deletes the landing page given it's id
	 * @param id The id of the page
	 * @throws EntityNotFoundException
	 * @throws DataIntegrityException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteLandingPage(@PathVariable("id") ObjectId id) throws EntityNotFoundException, DataIntegrityException {
		LandingPage p = service.getLandingPageById(id);
		service.deleteLandingPage(p);
	}
	
	
	/**
	 * Returns a list of content versions for the document with the specified id
	 * @param lpId The id of the document
	 * @return The list of versions available for this document
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "/{id}/contents", method = RequestMethod.GET)
	public List<VersionedContentIndexDTO> getRecentVersionedContent(@PathVariable("id") ObjectId lpId) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(lpId);
		return asDTO(service.getContentVersions(p), VersionedContentIndexDTO.class);
	}
	
	
	@RequestMapping(value = "/{id}/contents/current", method = RequestMethod.GET)
	public VersionedContent getCurrentContent(@PathVariable("id") ObjectId page) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(page);
		return service.getRecentContentVersionByPage(p);
	}
	
	/**
	 * Returns the versioned content given it's id
	 * @param pageId The id of the landing page
	 * @param contentId The id of the versioned content
	 * @return The versioned content
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "/{id}/content/{cid}", method = RequestMethod.GET)
	public VersionedContentDTO getContentById(@PathVariable("id") ObjectId pageId, @PathVariable("cid") ObjectId contentId) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(pageId);
		return asDTO(service.getContentVersionById(p, contentId), VersionedContentDTO.class);
		
	}

	/**
	 * Saves a new content version for the specified document
	 * @param lpId The id of the document to save the version
	 * @param dto The DTO containing the data of the version to be saved
	 * @return
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "/{id}/contents", method = RequestMethod.POST)
	public VersionedContentDTO saveContent(@PathVariable("id") ObjectId lpId, @RequestBody VersionedContentDTO dto) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(lpId);
		checkOnNull(p);
		
		VersionedContent c = new VersionedContent();
		c.setType(ContentType.HTML);
		c.setContent(dto.getContent());
		return asDTO(service.createContentVersion(p, c), VersionedContentDTO.class);
	}

	
	/**
	 * Creates a preview for the specified landing page
	 * @param pageId The page
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "/{id}/preview", method = RequestMethod.PUT)
	public void publishPreview(@PathVariable("id") ObjectId pageId) throws EntityNotFoundException {
		LandingPage page = service.getLandingPageById(pageId);
		service.publishPreview(page);
	}
	
	@RequestMapping(value = "/{id}/publish", method = RequestMethod.PUT)
	public LandingPage publishLive(@PathVariable("id") ObjectId pageId) throws EntityNotFoundException {
		LandingPage page = service.getLandingPageById(pageId);
		return service.publishLive(page);
	}
	
	@RequestMapping(value = "/{id}/publish", method = RequestMethod.DELETE)
	public LandingPage unpublishLive(@PathVariable("id") ObjectId pageId) throws BustaMailException {
		LandingPage page = service.getLandingPageById(pageId);
		return service.unpublishLive(page);
	}
}