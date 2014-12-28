package de.mfischbo.bustamail.landingpage.web;

import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.dto.StaticPageDTO;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;
import de.mfischbo.bustamail.vc.dto.VersionedContentIndexDTO;

@RestController
@RequestMapping("/api/landingpages")
public class RestStaticPageController extends BaseApiController {

	@Inject
	LandingPageService			service;
	
	@RequestMapping(value = "/{lpid}/staticpages", method = RequestMethod.GET)
	public List<StaticPage> getStaticPages(@PathVariable("lpid") ObjectId lpId) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(lpId);
		return p.getStaticPages();
	}
	
	@RequestMapping(value = "/{lpid}/staticpages/{sid}", method = RequestMethod.GET)
	public StaticPageDTO getStaticPageById(@PathVariable("lpid") ObjectId lpId, @PathVariable("sid") ObjectId sid) throws EntityNotFoundException {
		StaticPage page = service.getStaticPageById(sid);
		StaticPageDTO p = asDTO(page, StaticPageDTO.class);
		
		VersionedContent c = service.getRecentContentVersionByPage(page);
		p.setHtmlContent(asDTO(c, VersionedContentDTO.class));
		return p;
	}

	@RequestMapping(value = "/{lpid}/staticpages", method = RequestMethod.POST)
	public StaticPage createStaticPage(@PathVariable("lpid") ObjectId parentId, @RequestBody StaticPage dto) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(parentId);
		return service.createStaticPage(p, dto);
	}
	
	@RequestMapping(value = "/{lpid}/staticpages/{spid}", method = RequestMethod.PATCH)
	public StaticPage updateStaticPage(
			@PathVariable("lpid") ObjectId parentId, @PathVariable("spid") ObjectId pageId, 
			@RequestBody StaticPage dto) throws EntityNotFoundException {
		return service.updateStaticPage(dto);
	}
	
	@RequestMapping(value = "/{lpid}/staticpages/{spid}", method = RequestMethod.DELETE)
	public void deleteStaticPage(@PathVariable("lpid") ObjectId parentId, @PathVariable("spid") ObjectId pageId) throws EntityNotFoundException {
		StaticPage p = service.getStaticPageById(pageId);
		service.deleteStaticPage(p);
	}
	
	
	/**
	 * Returns a list of content versions for the document with the specified id
	 * @param lpId The id of the document
	 * @return The list of versions available for this document
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "/{lpid}/staticpages/{id}/content", method = RequestMethod.GET)
	public List<VersionedContentIndexDTO> getRecentVersionedContent(@PathVariable("id") ObjectId lpId) throws EntityNotFoundException {
		StaticPage p = service.getStaticPageById(lpId);
		return asDTO(service.getContentVersions(p), VersionedContentIndexDTO.class);
	}
	
	@RequestMapping(value = "/{lpid}/staticpages/{id}/content/{cid}", method = RequestMethod.GET)
	public VersionedContentDTO getContentById(@PathVariable("id") ObjectId pageId, @PathVariable("cid") ObjectId contentId) throws EntityNotFoundException {
		StaticPage p = service.getStaticPageById(pageId);
		return asDTO(service.getContentVersionById(p, contentId), VersionedContentDTO.class);
	}
	
	/**
	 * Saves a new content version for the specified document
	 * @param lpId The id of the document to save the version
	 * @param dto The DTO containing the data of the version to be saved
	 * @return
	 * @throws EntityNotFoundException
	 */
	@RequestMapping(value = "/{lpid}/staticpages/{id}/content", method = RequestMethod.POST)
	public VersionedContentDTO saveContent(@PathVariable("id") ObjectId lpId, @RequestBody VersionedContentDTO dto) throws EntityNotFoundException {
		StaticPage p = service.getStaticPageById(lpId);
		
		VersionedContent c = new VersionedContent();
		c.setType(ContentType.HTML);
		c.setContent(dto.getContent());
		return asDTO(service.createContentVersion(p, c), VersionedContentDTO.class);
	}
}
