package de.mfischbo.bustamail.mailing.web;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

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

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.domain.VersionedContent;
import de.mfischbo.bustamail.mailing.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;
import de.mfischbo.bustamail.mailing.dto.MailingDTO;
import de.mfischbo.bustamail.mailing.dto.MailingIndexDTO;
import de.mfischbo.bustamail.mailing.dto.VersionedContentDTO;
import de.mfischbo.bustamail.mailing.dto.VersionedContentIndexDTO;
import de.mfischbo.bustamail.mailing.service.MailingService;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.service.MailingListService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.service.TemplateService;

@RestController
@RequestMapping("/api/mailings")
public class RestMailingController extends BaseApiController {

	@Inject
	private MailingService			service;

	@Inject
	private TemplateService			tService;
	
	@Inject
	private MailingListService		mListService;
	
	@RequestMapping(value = "/unit/{owner}", method = RequestMethod.GET)
	public Page<MailingIndexDTO> getAllMailings(@PathVariable("owner") UUID owner, @PageableDefault(page=0, size=30) Pageable page) {
		return asDTO(service.getAllMailings(owner, page), MailingIndexDTO.class, page);
	}

	@RequestMapping(value = "/unit/{owner}", method = RequestMethod.POST)
	public MailingDTO createMailing(@PathVariable("owner") UUID owner, @RequestBody MailingDTO dto) throws Exception {

		Mailing m = new Mailing();
		m.setDateCreated(DateTime.now());
		m.setDateModified(DateTime.now());
		m.setOwner(owner);
		m.setReplyAddress(dto.getReplyAddress());
		m.setSenderAddress(dto.getSenderAddress());
		m.setSenderName(dto.getSenderName());
		m.setSubject(dto.getSubject());
	
		if (dto.getTemplate() != null && dto.getTemplate().getId() != null) {
			Template t = tService.getTemplateById(dto.getTemplate().getId());
			m.setTemplate(t);
		}
		return asDTO(service.createMailing(m), MailingDTO.class);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public MailingDTO getMailingById(@PathVariable("id") UUID mailingId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		
		// fetch most recent html / text content
		VersionedContent chtml = service.getRecentContent(m, ContentType.HTML);
		VersionedContent cText = service.getRecentContent(m, ContentType.Text);
	
		MailingDTO retval = asDTO(m, MailingDTO.class);
		retval.setHtmlContent(asDTO(chtml, VersionedContentDTO.class));
		retval.setTextContent(asDTO(cText, VersionedContentDTO.class));
		
		return retval;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public MailingDTO updateMailing(@PathVariable("owner") UUID owner, @PathVariable("id") UUID mailingId, @RequestBody MailingIndexDTO mailing) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		
		m.setDateModified(DateTime.now());
		m.setReplyAddress(mailing.getReplyAddress());
		m.setSenderAddress(mailing.getSenderAddress());
		m.setSenderName(mailing.getSenderName());
		m.setSubject(mailing.getSubject());
		return asDTO(service.updateMailing(m), MailingDTO.class);
	}

	
	@RequestMapping(value = "/{id}/content", method = RequestMethod.GET)
	public List<VersionedContentIndexDTO> getAllVersions(@PathVariable("id") UUID mailingId, 
			@RequestParam(value = "type", required = false) List<ContentType> types) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		return asDTO(service.getContentVersions(m, types), VersionedContentIndexDTO.class);
	}
	
	/**
	 * Request the approval for the mailing with the specified id
	 * @param id The id of the mailing approval is requested for
	 * @throws EntityNotFoundException When no mailing with that id could be found
	 */
	@RequestMapping(value = "/{id}/requestApproval", method = RequestMethod.GET)
	public void requestApproval(@PathVariable("id") UUID id) throws EntityNotFoundException {
		Mailing m = service.getMailingById(id);
		service.requestApproval(m);
	}

	/**
	 * Approves the mailing with the specified id to be published
	 * @param id The id of the mailing to be published
	 * @throws EntityNotFoundException When no mailing with that id could be found
	 */
	@RequestMapping(value = "/{id}/approve", method = RequestMethod.PUT)
	public void approveMailing(@PathVariable("id") UUID id) throws EntityNotFoundException {
		Mailing m = service.getMailingById(id);
		service.approveMailing(m);
	}
	
	@RequestMapping(value = "/{id}/content/{cid}", method = RequestMethod.GET)
	public VersionedContentDTO getVersionById(@PathVariable("id") UUID mailingId, @PathVariable("cid") UUID contentId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		return asDTO(service.getContentById(m, contentId), VersionedContentDTO.class);
	}
	
	
	@RequestMapping(value = "/{id}/content", method = RequestMethod.POST)
	public VersionedContentDTO createContentVersion(@PathVariable("id") UUID mailingId, @RequestBody VersionedContentDTO dto) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		
		VersionedContent c = new VersionedContent();
		c.setContent(dto.getContent());
		c.setType(dto.getType());
		return asDTO(service.createContentVersion(m, c), VersionedContentDTO.class);
	}
	
	@RequestMapping(value = "/{id}/subscription-lists/{lid}", method = RequestMethod.PUT)
	public void attachSubscriptionList(@PathVariable("id") UUID mailingId, @PathVariable("lid") UUID listId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		SubscriptionList l = mListService.getSubscriptionListById(listId);
		service.attachSubscriptionList(m, l);
	}
	
	@RequestMapping(value = "/{id}/subscription-lists/{lid}", method = RequestMethod.DELETE)
	public void removeSubscriptionList(@PathVariable("id") UUID mailingId, @PathVariable("lid") UUID listId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		SubscriptionList l = mListService.getSubscriptionListById(listId);
		service.removeSubscriptionList(m, l);
	}
	
	@RequestMapping(value = "/{id}/preview", method = RequestMethod.PUT)
	public void sendPreviewMailing(@PathVariable("id") UUID mailingId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		service.sendPreview(m);
	}
	
	@RequestMapping(value = "/linkcheck", method = RequestMethod.POST)
	public List<HyperlinkDTO> checkHyperlinkConnectivity(@RequestBody List<HyperlinkDTO> dtos) throws Exception {
		return service.checkHyperlinkConnectivity(dtos);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteMailing(@PathVariable("id") UUID mailingId) throws Exception {
		Mailing m = service.getMailingById(mailingId);
		service.deleteMailing(m);
	}
}
