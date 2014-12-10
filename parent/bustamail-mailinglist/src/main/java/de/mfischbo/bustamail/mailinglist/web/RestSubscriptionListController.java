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

import de.mfischbo.bustamail.annotation.IntegrationTested;
import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListSummaryDTO;
import de.mfischbo.bustamail.mailinglist.service.MailingListService;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.service.SecurityService;

/**
 * RESTful controller for subscription lists and subscription list management.
 * @author M. Fischboeck
 *
 */
@RestController
@RequestMapping(value = "/api/subscription-lists")
public class RestSubscriptionListController extends BaseApiController {

	@Inject
	private	MailingListService	service;

	@Inject
	private SecurityService		secService;
	
	
	/**
	 * Returns all subscription lists for the given owner
	 * @param ownerId The id of the org unit
	 * @param page The page parameters
	 * @return A page of subscription lists
	 * @throws Exception
	 */
	@IntegrationTested
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<SubscriptionListDTO> getAllSubscriptionLists(
			@RequestParam(value = "owner", required = true) UUID ownerId, 
			@PageableDefault(page = 0, size = 30, sort = "name") Pageable page) throws Exception {
		OrgUnit owner = secService.getOrgUnitById(ownerId);
		return asDTO(service.getAllMailingLists(owner, page), SubscriptionListDTO.class, page);
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public Page<SubscriptionListDTO> findSubscriptionLists(
			@RequestParam(value = "owner", required = true) UUID ownerId,
			@RequestParam(value = "q", required = true) String query,
			@PageableDefault(page = 0, size = 30, sort = "name") Pageable page) throws Exception {
		OrgUnit owner = secService.getOrgUnitById(ownerId);
		return asDTO(service.findSubscriptionLists(owner, query, page), SubscriptionListDTO.class, page);
	}
	
	
	/**
	 * Creates a new subscription list
	 * @param list The list to be created
	 * @return The created list
	 * @throws Exception
	 */
	@IntegrationTested
	@RequestMapping(value = "", method = RequestMethod.POST)
	public SubscriptionListDTO createSubscriptionList(
			@RequestBody SubscriptionListDTO list) throws Exception {
		return asDTO(service.createSubscriptionList(list), SubscriptionListDTO.class);
	}
	
	/**
	 * Returns the list with the given id
	 * @param id The id of the list to be returned
	 * @return The list
	 * @throws Exception
	 */
	@IntegrationTested
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public SubscriptionListDTO getSubscriptionListById(@PathVariable("id") UUID id) throws Exception {
		return asDTO(service.getSubscriptionListById(id), SubscriptionListDTO.class);
	}
	
	
	@RequestMapping(value = "/{id}/summary", method = RequestMethod.GET)
	public SubscriptionListSummaryDTO getSummaryBySubscriptionListId(@PathVariable("id") UUID id) throws Exception {
		SubscriptionList list = service.getSubscriptionListById(id);
		SubscriptionListSummaryDTO dto = new SubscriptionListSummaryDTO();
		dto.setSubscriptionsActive(service.getSubscriptionCountByState(list, State.ACTIVE));
		dto.setSubscriptionsPending(service.getSubscriptionCountByState(list, State.OPTIN));
		dto.setSubscriptionsInactive(service.getSubscriptionCountByState(list, State.INACTIVE));
		return dto;
	}
	
	/**
	 * Updates a subscription list
	 * @param listId The id of the list to be updated
	 * @param dto The DTO containing the lists new data
	 * @return 
	 * @throws Exception
	 */
	@IntegrationTested
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public SubscriptionListDTO updateSubscriptionList(@PathVariable("id") UUID listId, 
			@RequestBody SubscriptionListDTO dto) throws Exception {
		return asDTO(service.updateSubscriptionList(dto), SubscriptionListDTO.class);
	}
	
	
	/**
	 * Deletes the subscription list
	 * @param id The id of the list to be deleted
	 * @throws Exception 
	 */
	@IntegrationTested
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteSubscriptionList(@PathVariable("id") UUID id) throws Exception {
		SubscriptionList l = service.getSubscriptionListById(id);
		service.deleteSubscriptionList(l);
	}
}
