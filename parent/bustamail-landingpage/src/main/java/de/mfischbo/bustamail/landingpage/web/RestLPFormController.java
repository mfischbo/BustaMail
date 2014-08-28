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
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.dto.LPFormDTO;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;

@RestController
@RequestMapping(value = "/api/landingpages")
public class RestLPFormController extends BaseApiController {

	@Inject
	LandingPageService		service;
	
	@RequestMapping(value = "/{lpid}/forms", method = RequestMethod.GET)
	public List<LPFormDTO> getFormsByLandingPage(@PathVariable("lpid") UUID pageId) throws EntityNotFoundException {
		
		LandingPage p = service.getLandingPageById(pageId);
		return asDTO(p.getForms(), LPFormDTO.class);
	}

	@RequestMapping(value = "/{lpid}/forms/{fid}", method = RequestMethod.GET)
	public LPFormDTO getFormById(@PathVariable("lpid") UUID pageId, @PathVariable("fid") UUID formId) throws EntityNotFoundException {
		return asDTO(service.getFormById(formId), LPFormDTO.class);
	}

	@RequestMapping(value = "/{lpid}/forms", method = RequestMethod.POST)
	public LPFormDTO createForm(@PathVariable("lpid") UUID pageId, @RequestBody LPFormDTO dto) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(pageId);
		return asDTO(service.createForm(p, dto), LPFormDTO.class);
	}
	
	@RequestMapping(value = "/{lpid}/forms/{fid}", method = RequestMethod.PATCH)
	public LPFormDTO updateForm(@PathVariable("lpid") UUID pageId, @PathVariable("fid") UUID formId, @RequestBody LPFormDTO dto) throws EntityNotFoundException {
		return asDTO(service.updateForm(dto), LPFormDTO.class);
	}
	
	@RequestMapping(value = "/{lpid}/forms/{fid}", method = RequestMethod.DELETE)
	public void deleteForm(@PathVariable("lpid") UUID pageId, @PathVariable("fid") UUID formId) throws EntityNotFoundException {
		LPForm form = service.getFormById(formId);
		service.deleteForm(form);
	}
}
