package de.mfischbo.bustamail.landingpage.web;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

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
import de.mfischbo.bustamail.landingpage.dto.StaticPageIndexDTO;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

@RestController
@RequestMapping("/api/landingpages")
public class RestStaticPageController extends BaseApiController {

	@Inject
	LandingPageService			service;
	
	@RequestMapping(value = "/{lpid}/staticpages", method = RequestMethod.GET)
	public List<StaticPageIndexDTO> getStaticPages(@PathVariable("lpid") UUID lpId) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(lpId);
		return asDTO(service.getStaticPages(p), StaticPageIndexDTO.class);
	}
	
	@RequestMapping(value = "/{lpid}/staticpages/{sid}", method = RequestMethod.GET)
	public StaticPageDTO getStaticPageById(@PathVariable("lpid") UUID lpId, @PathVariable("sid") UUID sid) throws EntityNotFoundException {
		StaticPage page = service.getStaticPageById(sid);
		StaticPageDTO p = asDTO(page, StaticPageDTO.class);
		
		VersionedContent c = service.getRecentContentVersionByPage(page);
		p.setHtmlContent(asDTO(c, VersionedContentDTO.class));
		return p;
	}

	@RequestMapping(value = "/{lpid}/staticpages", method = RequestMethod.POST)
	public StaticPageDTO createStaticPage(@PathVariable("lpid") UUID parentId, @RequestBody StaticPageIndexDTO dto) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(parentId);
		return asDTO(service.createStaticPage(p, dto), StaticPageDTO.class);
	}
	
	@RequestMapping(value = "/{lpid}/staticpages/{spid}", method = RequestMethod.PATCH)
	public StaticPageDTO updateStaticPage(
			@PathVariable("lpid") UUID parentId, @PathVariable("spid") UUID pageId, 
			@RequestBody StaticPageIndexDTO dto) throws EntityNotFoundException {
		return asDTO(service.updateStaticPage(dto), StaticPageDTO.class);
	}
	
	@RequestMapping(value = "/{lpid}/staticpages/{spid}", method = RequestMethod.DELETE)
	public void deleteStaticPage(@PathVariable("lpid") UUID parentId, @PathVariable("spid") UUID pageId) throws EntityNotFoundException {
		StaticPage p = service.getStaticPageById(pageId);
		service.deleteStaticPage(p);
	}
}
