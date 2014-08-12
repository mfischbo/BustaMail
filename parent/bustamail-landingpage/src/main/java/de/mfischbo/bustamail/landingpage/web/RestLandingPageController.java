package de.mfischbo.bustamail.landingpage.web;

import java.util.Collection;
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
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.dto.LandingPageDTO;
import de.mfischbo.bustamail.landingpage.dto.LandingPageIndexDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageIndexDTO;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

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
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public LandingPageDTO getLandingPageById(@PathVariable("id") UUID id) throws EntityNotFoundException {
		LandingPage page = service.getLandingPageById(id);
		LandingPageDTO retval = asDTO(page, LandingPageDTO.class);
		retval.setContent(asDTO(service.getRecentContentVersionByPage(page), VersionedContentDTO.class));
		return retval;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public LandingPageIndexDTO createLandingPage(@PathVariable("owner") UUID owner, @RequestBody LandingPageIndexDTO page) throws EntityNotFoundException {
		return asDTO(service.createLandingPage(page), LandingPageIndexDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public LandingPageIndexDTO updateLandingPage(@RequestBody LandingPageIndexDTO page) throws EntityNotFoundException {
		return asDTO(service.updateLandingPage(page), LandingPageIndexDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteLandingPage(@PathVariable("id") UUID id) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(id);
		service.deleteLandingPage(p);
	}
	
	@RequestMapping(value = "/{id}/statics", method = RequestMethod.GET)
	public Collection<StaticPageIndexDTO> getStaticPages(@PathVariable("id") UUID lpId) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(lpId);
		return asDTO(service.getStaticPages(p), StaticPageIndexDTO.class);
	}
	
	@RequestMapping(value = "/{id}/statics/{sid}", method = RequestMethod.GET)
	public StaticPageDTO getStaticPageById(@PathVariable("id") UUID pageId, @PathVariable("sid") UUID sid) throws EntityNotFoundException {
		StaticPage page = service.getStaticPageById(sid);
		StaticPageDTO retval = asDTO(page, StaticPageDTO.class);
		retval.setContent(asDTO(service.getRecentContentVersionByPage(page), VersionedContentDTO.class));
		return retval;
	}
}