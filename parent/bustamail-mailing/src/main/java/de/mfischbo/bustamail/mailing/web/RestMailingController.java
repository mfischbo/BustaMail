package de.mfischbo.bustamail.mailing.web;

import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;
import de.mfischbo.bustamail.mailing.dto.MailingDTO;
import de.mfischbo.bustamail.mailing.dto.MailingIndexDTO;
import de.mfischbo.bustamail.mailing.service.MailingService;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.service.MailingListService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.service.TemplateService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;
import de.mfischbo.bustamail.vc.dto.VersionedContentIndexDTO;

/**
 * RESTful controller to handle all actions the mailing provides.
 * 
 * @author M. Fischboeck
 *
 */
@RestController
@RequestMapping("/api/mailings")
public class RestMailingController extends BaseApiController {

	@Inject
	private MailingService service;
	
	@Inject
	private TemplateService	tService;

	@Inject
	private MailingListService mListService;

	/**
	 * Returns all mailings owned by the specified owner id
	 * 
	 * @param owner
	 *            The id of the owner
	 * @param page
	 *            The page parameters
	 * @return A page with mailings
	 */
	@IntegrationTested
	@RequestMapping(value = "/unit/{owner}", method = RequestMethod.GET)
	public Page<MailingIndexDTO> getAllMailings(
			@PathVariable("owner") ObjectId owner,
			@PageableDefault(page = 0, size = 30) Pageable page) {
		return asDTO(service.getAllMailings(owner, page),
				MailingIndexDTO.class, page);
	}

	/**
	 * Creates a new mailing for the specified owner
	 * 
	 * @param owner
	 *            The id of the mailings owner
	 * @param dto
	 *            The DTO holding the data for the mailing
	 * @return The DTO representing the persisted entity
	 * @throws Exception
	 *             On any failure
	 */
	@RequestMapping(value = "/unit/{owner}", method = RequestMethod.POST)
	public Mailing createMailing(@PathVariable("owner") ObjectId owner,
			@RequestBody Mailing m) throws Exception {

		m.setDateCreated(DateTime.now());
		m.setDateModified(DateTime.now());
		m.setOwner(owner);

		TemplatePack tp = tService.getTemplatePackById(m.getTemplatePack().getId());
		Template t = tp.getTemplates().stream()
			.filter(q -> q.getId().equals(m.getTemplateId()))
			.findFirst()
			.get();
		return service.createMailing(m, t);
	}

	/**
	 * Returns the mailing with the specified id
	 * 
	 * @param mailingId
	 *            The id of the mailing to be returned
	 * @return The DTO representing the persisted mailing
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Mailing getMailingById(@PathVariable("id") ObjectId mailingId)
			throws Exception {
		return service.getMailingById(mailingId);
	}

	/**
	 * Updates the mailing
	 * 
	 * @param owner
	 *            The owner of the mailing
	 * @param mailingId
	 *            The id of the mailing to be updated
	 * @param mailing
	 *            The DTO containing the data for the mailing
	 * @return The DTO representing the persisted entity of the mailing
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public MailingDTO updateMailing(@PathVariable("owner") ObjectId owner,
			@PathVariable("id") ObjectId mailingId,
			@RequestBody MailingIndexDTO mailing) throws Exception {
		Mailing m = service.getMailingById(mailingId);

		m.setDateModified(DateTime.now());
		m.setReplyAddress(mailing.getReplyAddress());
		m.setSenderAddress(mailing.getSenderAddress());
		m.setSenderName(mailing.getSenderName());
		m.setSubject(mailing.getSubject());
		return asDTO(service.updateMailing(m), MailingDTO.class);
	}

	/**
	 * Retrieves the list of VersionedContent headers
	 * 
	 * @param mailingId
	 *            The if of the mailing to get the versioned content headers
	 * @param types
	 *            The type of the versioned content (either HTML or Text)
	 * @return The list of versioned content headers for the specified mailing
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}/content", method = RequestMethod.GET)
	public List<VersionedContentIndexDTO> getAllVersions(
			@PathVariable("id") ObjectId mailingId,
			@RequestParam(value = "type", required = false) List<ContentType> types)
			throws Exception {
		Mailing m = service.getMailingById(mailingId);
		return asDTO(service.getContentVersions(m, types),
				VersionedContentIndexDTO.class);
	}

	@RequestMapping(value = "/{id}/content/current", method = RequestMethod.GET)
	public VersionedContent getCurrentContent(
			@PathVariable("id") ObjectId mailingId,
			@RequestParam(value = "type", required = false, defaultValue = "HTML") ContentType type) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		return service.getRecentContent(m, type);
	}
	
	/**
	 * Request the approval for the mailing with the specified id
	 * 
	 * @param id
	 *            The id of the mailing approval is requested for
	 * @throws EntityNotFoundException
	 *             When no mailing with that id could be found
	 */
	@RequestMapping(value = "/{id}/requestApproval", method = RequestMethod.GET)
	public void requestApproval(@PathVariable("id") ObjectId id)
			throws EntityNotFoundException {
		Mailing m = service.getMailingById(id);
		service.requestApproval(m);
	}

	/**
	 * Approves the mailing with the specified id to be published
	 * 
	 * @param id
	 *            The id of the mailing to be published
	 * @throws EntityNotFoundException
	 *             When no mailing with that id could be found
	 */
	@RequestMapping(value = "/{id}/approve", method = RequestMethod.PUT)
	public void approveMailing(@PathVariable("id") ObjectId id)
			throws EntityNotFoundException {
		Mailing m = service.getMailingById(id);
		service.approveMailing(m);
	}

	/**
	 * Returns the full versioned content for the specified mailing and content
	 * id
	 * 
	 * @param mailingId
	 *            The id of the mailing
	 * @param contentId
	 *            The id of the content
	 * @return The DTO representing the persisted entity
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}/content/{cid}", method = RequestMethod.GET)
	public VersionedContentDTO getVersionById(
			@PathVariable("id") ObjectId mailingId,
			@PathVariable("cid") ObjectId contentId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		return asDTO(service.getContentById(m, contentId),
				VersionedContentDTO.class);
	}

	/**
	 * Creates a new content version for the specified mailing
	 * 
	 * @param mailingId
	 *            The id of the mailing
	 * @param dto
	 *            The DTO containing the data of the versioned content to be
	 *            stored
	 * @return The DTO representing the persisted entity
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}/content", method = RequestMethod.POST)
	public VersionedContentDTO createContentVersion(
			@PathVariable("id") ObjectId mailingId,
			@RequestBody VersionedContentDTO dto) throws Exception {
		Mailing m = service.getMailingById(mailingId);

		VersionedContent c = new VersionedContent();
		c.setContent(dto.getContent());
		c.setType(dto.getType());
		return asDTO(service.createContentVersion(m, c),
				VersionedContentDTO.class);
	}

	/**
	 * Adds the subscription list with the specified id to the mailings lists
	 * 
	 * @param mailingId
	 *            The id of the mailing
	 * @param listId
	 *            The id of the subscription list to be added to the lists the
	 *            mailing will be published to
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}/subscription-lists/{lid}", method = RequestMethod.PUT)
	public void attachSubscriptionList(@PathVariable("id") ObjectId mailingId,
			@PathVariable("lid") ObjectId listId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		SubscriptionList l = mListService.getSubscriptionListById(listId);
		service.attachSubscriptionList(m, l);
	}

	/**
	 * Removes the subscription list from the lists the mailing will be sent to
	 * 
	 * @param mailingId
	 *            The id of the mailing
	 * @param listId
	 *            The id of the list to be removed
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}/subscription-lists/{lid}", method = RequestMethod.DELETE)
	public void removeSubscriptionList(@PathVariable("id") ObjectId mailingId,
			@PathVariable("lid") ObjectId listId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		SubscriptionList l = mListService.getSubscriptionListById(listId);
		service.removeSubscriptionList(m, l);
	}

	/**
	 * Sends a preview of the mailing to the current users email account
	 * 
	 * @param mailingId
	 *            The id of the mailing to send the preview for
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}/preview", method = RequestMethod.PUT)
	public void sendPreviewMailing(@PathVariable("id") ObjectId mailingId)
			throws Exception {
		Mailing m = service.getMailingById(mailingId);
		service.sendPreview(m);
	}

	/**
	 * Publishes the mailing with the specified id to all lists the mailing
	 * holds.
	 * 
	 * @param mailingId
	 *            The id of the mailing to be published
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}/publish", method = RequestMethod.PUT)
	public void publishMailing(@PathVariable("id") ObjectId mailingId)
			throws Exception {
		Mailing m = service.getMailingById(mailingId);
		service.publishMailing(m);
	}

	/**
	 * Proxies a call to check if a link is valid
	 * 
	 * @param dtos
	 *            The DTO containing the target for the link
	 * @return The DTO containing the result of the link checks
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/linkcheck", method = RequestMethod.POST)
	public List<HyperlinkDTO> checkHyperlinkConnectivity(
			@RequestBody List<HyperlinkDTO> dtos) throws Exception {
		return service.checkHyperlinkConnectivity(dtos);
	}

	/**
	 * Deletes the mailing with the specified id
	 * 
	 * @param mailingId
	 *            The id of the mailing to be deleted
	 * @throws Exception
	 *             On any error
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteMailing(@PathVariable("id") ObjectId mailingId)
			throws Exception {
		Mailing m = service.getMailingById(mailingId);
		service.deleteMailing(m);
	}
}
