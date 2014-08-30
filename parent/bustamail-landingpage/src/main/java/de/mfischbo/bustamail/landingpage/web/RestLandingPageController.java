package de.mfischbo.bustamail.landingpage.web;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

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
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.dto.LandingPageDTO;
import de.mfischbo.bustamail.landingpage.dto.LandingPageIndexDTO;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;
import de.mfischbo.bustamail.vc.dto.VersionedContentIndexDTO;

@RestController
@RequestMapping("/api/landingpages")
public class RestLandingPageController extends BaseApiController {

	@Inject
	private LandingPageService		service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<LandingPageIndexDTO> getAllLandingPages(@RequestParam("owner") UUID owner, 
			@PageableDefault(size=30, value=0) Pageable page) {
		return asDTO(service.getLandingPagesByOwner(owner, page), LandingPageIndexDTO.class, page);
	}

	/**
	 * Returns the landing page by the specified id
	 * @param id The id of the landing page
	 * @return The landing page
	 * @throws EntityNotFoundException If no such landing page exists
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public LandingPageDTO getLandingPageById(@PathVariable("id") UUID id) throws EntityNotFoundException {
		LandingPage page = service.getLandingPageById(id);
		LandingPageDTO retval = asDTO(page, LandingPageDTO.class);
		retval.setHtmlContent(asDTO(service.getRecentContentVersionByPage(page), VersionedContentDTO.class));
		return retval;
	}
	
	/**
	 * Creates a new landing page
	 * @param page The page to be created
	 * @return
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public LandingPageIndexDTO createLandingPage(@RequestBody LandingPageIndexDTO page) throws EntityNotFoundException {
		return asDTO(service.createLandingPage(page), LandingPageIndexDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public LandingPageDTO updateLandingPage(@RequestBody LandingPageDTO page) throws EntityNotFoundException {
		return asDTO(service.updateLandingPage(page), LandingPageDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteLandingPage(@PathVariable("id") UUID id) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(id);
		service.deleteLandingPage(p);
	}
	
	
	/**
	 * Returns a list of content versions for the document with the specified id
	 * @param lpId The id of the document
	 * @return The list of versions available for this document
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "/{id}/content", method = RequestMethod.GET)
	public List<VersionedContentIndexDTO> getRecentVersionedContent(@PathVariable("id") UUID lpId) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(lpId);
		return asDTO(service.getContentVersions(p), VersionedContentIndexDTO.class);
	}
	
	@RequestMapping(value = "/{id}/content/{cid}", method = RequestMethod.GET)
	public VersionedContentDTO getContentById(@PathVariable("id") UUID pageId, @PathVariable("cid") UUID contentId) throws EntityNotFoundException {
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
	@RequestMapping(value = "/{id}/content", method = RequestMethod.POST)
	public VersionedContentDTO saveContent(@PathVariable("id") UUID lpId, @RequestBody VersionedContentDTO dto) throws EntityNotFoundException {
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
	public void publishPreview(@PathVariable("id") UUID pageId) throws EntityNotFoundException {
		LandingPage page = service.getLandingPageById(pageId);
		service.publishPreview(page);
	}
	
	@RequestMapping(value = "/{id}/publish", method = RequestMethod.PUT)
	public void publishLive(@PathVariable("id") UUID pageId) throws EntityNotFoundException {
		LandingPage page = service.getLandingPageById(pageId);
		service.publishLive(page);
	}
}