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
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.ImportResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.ParsingResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;
import de.mfischbo.bustamail.mailinglist.service.MailingListService;

@RestController
@RequestMapping("/api/subscription-lists/{slid}/subscriptions")
public class RestSubscriptionController extends BaseApiController {

	@Inject
	private MailingListService		service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<SubscriptionDTO> getSubscriptions(@PathVariable("slid") UUID listId, 
			@PageableDefault(page = 0, size = 30) Pageable page) throws Exception {
		SubscriptionList list = service.getSubscriptionListById(listId);
		return asDTO(service.getSubscriptionsByList(list, page), SubscriptionDTO.class, page);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public SubscriptionDTO getSubscriptionById(@PathVariable("slid") UUID listId, @PathVariable("id") UUID id) throws Exception {
		return asDTO(service.getSubscriptionById(id), SubscriptionDTO.class);
	}
	
	
	@RequestMapping(value = "/parse", method = RequestMethod.POST)
	public ParsingResultDTO parseImportFile(@RequestBody SubscriptionImportDTO dto) throws Exception {
		return null;
	}
	
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public ImportResultDTO importFile(@RequestBody SubscriptionImportDTO dto) throws Exception {
		return null;
	}
}
