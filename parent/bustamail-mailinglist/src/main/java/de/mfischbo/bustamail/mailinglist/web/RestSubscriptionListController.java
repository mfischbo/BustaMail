package de.mfischbo.bustamail.mailinglist.web;

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
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.mailinglist.service.MailingListService;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.service.SecurityService;

@RestController
@RequestMapping(value = "/api/subscription-lists")
public class RestSubscriptionListController extends BaseApiController {

	@Inject
	private	MailingListService			service;

	@Inject
	private SecurityService		secService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<SubscriptionListDTO> getAllSubscriptionLists(
			@RequestParam(value = "owner", required = true) UUID ownerId, 
			@PageableDefault(page = 0, size = 30, sort = "name") Pageable page) throws Exception {
		OrgUnit owner = secService.getOrgUnitById(ownerId);
		return asDTO(service.getAllMailingLists(owner, page), SubscriptionListDTO.class, page);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public SubscriptionListDTO createSubscriptionList(
			@RequestBody SubscriptionListDTO list) throws Exception {
		return asDTO(service.createSubscriptionList(list), SubscriptionListDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public SubscriptionListDTO getSubscriptionListById(@PathVariable("id") UUID id) throws Exception {
		return asDTO(service.getSubscriptionListById(id), SubscriptionListDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public SubscriptionListDTO updateSubscriptionList(@PathVariable("id") UUID listId, 
			@RequestBody SubscriptionListDTO dto) throws Exception {
		return asDTO(service.updateSubscriptionList(dto), SubscriptionListDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteSubscriptionList(@PathVariable("id") UUID id) throws Exception {
		SubscriptionList l = service.getSubscriptionListById(id);
		service.deleteSubscriptionList(l);
	}
}
