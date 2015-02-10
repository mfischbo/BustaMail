package de.mfischbo.bustamail.web;

import java.util.List;

import javax.inject.Inject;

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
import de.mfischbo.bustamail.optin.domain.OptinMail;
import de.mfischbo.bustamail.optin.service.OptinMailService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.dto.VersionedContentIndexDTO;

@RestController
@RequestMapping(value = "/api/optin")
public class RestOptinMailController extends BaseApiController {

	@Inject private OptinMailService	service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<OptinMail> getAllOptinMails(@PageableDefault Pageable page,
			@RequestParam(value = "owner", required=true) ObjectId owner) {
		return service.getAllOptinMails(owner, page);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public OptinMail getOptinMailById(@PathVariable("id") ObjectId id) {
		return service.getOptinMailById(id);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public OptinMail createOptinMail(@RequestBody OptinMail mail) throws Exception {
		return service.createOptinMail(mail);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public OptinMail updateOptinMail(@RequestBody OptinMail mail) {
		return service.updateOptinMail(mail);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteOptinMail(@PathVariable("id") ObjectId id) {
		OptinMail m = service.getOptinMailById(id);
		service.deleteOptinMail(m);
	}
	
	@RequestMapping(value = "/{id}/contents/current", method = RequestMethod.GET)
	public VersionedContent getCurrentContent(@PathVariable("id") ObjectId id,
			@RequestParam(value = "type", required = true, defaultValue = "HTML") ContentType type) {
		OptinMail m = service.getOptinMailById(id);
		return service.getCurrentContent(m, type);
	}
	
	@RequestMapping(value = "/{id}/contents", method = RequestMethod.GET)
	public List<VersionedContentIndexDTO> getContents(@PathVariable("id") ObjectId id,
			@RequestParam(value = "type", required = true, defaultValue = "HTML") ContentType type) {
		OptinMail m = service.getOptinMailById(id);
		return asDTO(service.getContents(m, type), VersionedContentIndexDTO.class);
	}
	
	@RequestMapping(value = "/{id}/contents", method = RequestMethod.POST)
	public VersionedContent createContentVersion(@PathVariable("id") ObjectId id,
			@RequestBody VersionedContent vc) {
		OptinMail m = service.getOptinMailById(id);
		return service.createContentVersion(m, vc);
	}
}