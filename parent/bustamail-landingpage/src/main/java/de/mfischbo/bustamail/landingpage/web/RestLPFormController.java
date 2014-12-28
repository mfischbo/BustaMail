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
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;

@RestController
@RequestMapping(value = "/api/landingpages")
public class RestLPFormController extends BaseApiController {

	@Inject
	LandingPageService		service;
	
	@RequestMapping(value = "/{lpid}/forms", method = RequestMethod.GET)
	public List<LPForm> getFormsByLandingPage(@PathVariable("lpid") ObjectId pageId) throws EntityNotFoundException {
		
		LandingPage p = service.getLandingPageById(pageId);
		return p.getForms();
	}

	@RequestMapping(value = "/{lpid}/forms/{fid}", method = RequestMethod.GET)
	public LPForm getFormById(@PathVariable("lpid") ObjectId pageId, @PathVariable("fid") ObjectId formId) throws EntityNotFoundException {
		return service.getFormById(formId);
	}

	@RequestMapping(value = "/{lpid}/forms", method = RequestMethod.POST)
	public LPForm createForm(@PathVariable("lpid") ObjectId pageId, @RequestBody LPForm dto) throws EntityNotFoundException {
		LandingPage p = service.getLandingPageById(pageId);
		return service.createForm(p, dto);
	}
	
	@RequestMapping(value = "/{lpid}/forms/{fid}", method = RequestMethod.PATCH)
	public LPForm updateForm(@PathVariable("lpid") ObjectId pageId, @PathVariable("fid") ObjectId formId, @RequestBody LPForm dto) throws EntityNotFoundException {
		return service.updateForm(dto);
	}
	
	@RequestMapping(value = "/{lpid}/forms/{fid}", method = RequestMethod.DELETE)
	public void deleteForm(@PathVariable("lpid") ObjectId pageId, @PathVariable("fid") ObjectId formId) throws EntityNotFoundException {
		LPForm form = service.getFormById(formId);
		service.deleteForm(form);
	}
}
