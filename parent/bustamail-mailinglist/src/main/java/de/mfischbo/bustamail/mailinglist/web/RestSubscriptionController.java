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
import org.springframework.web.multipart.MultipartFile;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.ImportResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.ParsingResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;
import de.mfischbo.bustamail.mailinglist.service.MailingListService;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

@RestController
@RequestMapping("/api/subscription-lists/{slid}/subscriptions")
public class RestSubscriptionController extends BaseApiController {

	@Inject
	private MailingListService		service;
	
	@Inject
	private MediaService			mediaService;
	
	
	/**
	 * Returns all subscriptions for the given mailing list
	 * @param listId The if of the mailing list
	 * @param page The paging parameters
	 * @return A page of subscriptions
	 * @throws Exception
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<SubscriptionDTO> getSubscriptions(@PathVariable("slid") UUID listId, 
			@PageableDefault(page = 0, size = 30) Pageable page) throws Exception {
		SubscriptionList list = service.getSubscriptionListById(listId);
		return asDTO(service.getSubscriptionsByList(list, page), SubscriptionDTO.class, page);
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public Page<SubscriptionDTO> findSubscriptions(@PathVariable("slid") UUID listId, 
			@RequestParam(value = "q", defaultValue = "", required = true) String query,
			@PageableDefault(page = 0, size = 30) Pageable page) throws Exception {
	
		SubscriptionList list = service.getSubscriptionListById(listId);
		return asDTO(service.findSubscriptions(list, query, page), SubscriptionDTO.class, page);
	}

	/**
	 * Returns a subscription by the given id
	 * @param listId The id of the list
	 * @param id The id of the subscription
	 * @return The subscription to the list
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public SubscriptionDTO getSubscriptionById(@PathVariable("slid") UUID listId, @PathVariable("id") UUID id) throws Exception {
		return asDTO(service.getSubscriptionById(id), SubscriptionDTO.class);
	}
	

	/**
	 * Removes a active subscription from the subscription list
	 * @param listId The id of the list to remove the subscription from
	 * @param id The id of the subscription
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void unsubscribeSubscription(@PathVariable("slid") UUID listId, @PathVariable("id") UUID id) throws Exception {
		Subscription s = service.getSubscriptionById(id);
		SubscriptionList list = service.getSubscriptionListById(listId);
		service.unsubscribeSubscription(list, s);
	}
	
	
	/**
	 * Uploads a file containing contacts to be imported into the given list 
	 * @param listId The id of the list to import the contacts to
	 * @param file The file
	 * @return Configuration for the import
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public SubscriptionImportDTO uploadImportFile(@PathVariable("slid") UUID listId, MultipartFile file) throws Exception {
		SubscriptionList l = service.getSubscriptionListById(listId);
		
		Media m = new Media();
		m.setData(file.getBytes());
		m.setName(file.getOriginalFilename());
		m.setOwner(l.getOwner());
		m = mediaService.createMedia(m);
		return service.getEstimatedFileSettings(m);
	}

	/**
	 * Parses an import file and returns a result
	 * @param listId The id of the mailing list
	 * @param dto The DTO containing the configuration for this import
	 * @return The results of the parsing process
	 * @throws Exception
	 */
	@RequestMapping(value = "/parse", method = RequestMethod.POST)
	public ParsingResultDTO parseImportFile(@PathVariable("slid") UUID listId, @RequestBody SubscriptionImportDTO dto) throws Exception {
	
		Media m = mediaService.getMediaById(dto.getMediaId());
		SubscriptionList list = service.getSubscriptionListById(listId);
		return service.parseImportFile(list, m, dto);
	}
	
	@RequestMapping(value = "/status", method = RequestMethod.POST)
	public ParsingResultDTO getParsingStatus(@PathVariable("slid") UUID listId, @RequestBody SubscriptionImportDTO dto) throws Exception {
		Media m = mediaService.getMediaById(dto.getMediaId());
		SubscriptionList list = service.getSubscriptionListById(listId);
		return service.parseForErrors(list, m, dto);
	}
	
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public ImportResultDTO importCurrent(@PathVariable("slid") UUID listId, @RequestBody SubscriptionImportDTO dto) throws Exception {
		Media m = mediaService.getMediaById(dto.getMediaId());
		SubscriptionList list = service.getSubscriptionListById(listId);
		return service.importSubscriptions(list, m, dto);
	}
}
