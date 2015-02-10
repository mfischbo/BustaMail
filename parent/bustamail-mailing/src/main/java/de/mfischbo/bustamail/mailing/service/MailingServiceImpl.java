package de.mfischbo.bustamail.mailing.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.ConfigurationException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.processor.MailingPreProcessor;
import de.mfischbo.bustamail.mailer.service.SimpleMailService;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;
import de.mfischbo.bustamail.mailing.repository.MailingRepository;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;

/**
 * Service class that handles all actions regarding a mailing.
 * The service provides CRUD operations for mailings, as well as content retrieval, 
 * publishing workflow and preview sending
 * @author M. Fischboeck 
 *
 */
@Service
public class MailingServiceImpl extends BaseService implements MailingService {

	@Inject
	private		SecurityService			secService;
	
	@Inject
	private		MailingRepository		mRepo;
	
	@Inject
	private		VersionedContentRepository	vcRepo;
	
	@Inject
	private		SubscriptionRepository		sRepo;
	
	@Inject
	private		Authentication				auth;
	
	@Inject
	private		SimpleMailService			simpleMailer;
	
	@Inject
	private		MailingPreProcessor			preProcessor;
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#getAllMailings(java.util.UUID, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<Mailing> getAllMailings(ObjectId owner, Pageable page) {
		return mRepo.findAllByOwner(owner, page);
	}

	
	@Override
	public Page<Mailing> getAllVisibleMailings(Pageable page) {
		
		MailingModulePermissionProvider prov = new MailingModulePermissionProvider();
		Set<Permission> perms = prov.getByNames("Mailings.USE_MAILINGS");
		Set<OrgUnit> units = secService.getOrgUnitsByCurrentUserWithPermissions(perms);
		Set<ObjectId> oids = new HashSet<>();
		
		units.forEach(u -> oids.add(u.getId()));
		return mRepo.getByOwnership(oids, page);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#getMailingById(java.util.UUID)
	 */
	@Override
	public Mailing getMailingById(ObjectId id) throws EntityNotFoundException {
		Mailing m = mRepo.findOne(id);
		checkOnNull(m);
		return m;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#createMailing(de.mfischbo.bustamail.mailing.domain.Mailing)
	 */
	@Override
	public Mailing createMailing(Mailing m, Template t) throws EntityNotFoundException {
	
		User current = (User) auth.getPrincipal();
		m.setUserCreated(current);
		m.setUserModified(current);
		m.setTemplateId(t.getId());
		m = mRepo.save(m);
		
		// create a versioned content
		VersionedContent html = new VersionedContent();
		html.setContent(t.getSource());
		html.setDateCreated(m.getDateCreated());
		html.setForeignId(m.getId());
		html.setUserCreated(m.getUserCreated());
		html.setType(ContentType.HTML);
		html = vcRepo.save(html);
		
		return m;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#updateMailing(de.mfischbo.bustamail.mailing.domain.Mailing)
	 */
	@Override
	public Mailing updateMailing(Mailing m) throws EntityNotFoundException {
		m.setDateModified(DateTime.now());
		m.setUserModified((User) auth.getPrincipal()); 
		return mRepo.save(m);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#deleteMailing(de.mfischbo.bustamail.mailing.domain.Mailing)
	 */
	@Override
	public void deleteMailing(Mailing m) throws EntityNotFoundException {
		mRepo.delete(m);
		List<VersionedContent> contents = vcRepo.findByForeignId(m.getId());
		vcRepo.delete(contents);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#getContentVersions(de.mfischbo.bustamail.mailing.domain.Mailing, java.util.List)
	 */
	@Override
	public List<VersionedContent> getContentVersions(Mailing m, List<ContentType> types) {
		
		List<VersionedContent> retval = new LinkedList<>();
		
		if (types != null && types.size() > 1) {
			retval = vcRepo.findByForeignIdAndType(m.getId(), types, new PageRequest(0, 500)).getContent();
		} else {
			retval = vcRepo.findByForeignId(m.getId());
		}
		return retval;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#getContentById(de.mfischbo.bustamail.mailing.domain.Mailing, java.util.ObjectId)
	 */
	@Override
	public VersionedContent getContentById(Mailing m, ObjectId contentId) throws EntityNotFoundException {
		VersionedContent retval = vcRepo.findOne(contentId);
		checkOnNull(retval);
		return retval;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#getRecentContent(de.mfischbo.bustamail.mailing.domain.Mailing, de.mfischbo.bustamail.mailing.domain.VersionedContent.ContentType)
	 */
	@Override
	public VersionedContent getRecentContent(Mailing m, ContentType type) {
		
		PageRequest preq = new PageRequest(0, 1, Sort.Direction.DESC, "dateCreated");
		Collection<ContentType> types = new LinkedList<>();
		types.add(type);
		Page<VersionedContent> result = vcRepo.findByForeignIdAndType(m.getId(), types , preq);
		if (result.getTotalElements() == 0)
			return null;
		return result.getContent().get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#createContentVersion(de.mfischbo.bustamail.mailing.domain.Mailing, de.mfischbo.bustamail.mailing.domain.VersionedContent)
	 */
	@Override
	public VersionedContent createContentVersion(Mailing m,
			VersionedContent c) {
		
		User current = (User) auth.getPrincipal();
		c.setForeignId(m.getId());
		c.setDateCreated(DateTime.now());
		c.setUserCreated(current);
		return vcRepo.save(c);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#attachSubscriptionList(de.mfischbo.bustamail.mailing.domain.Mailing, de.mfischbo.bustamail.mailinglist.domain.SubscriptionList)
	 */
	@Override
	public void attachSubscriptionList(Mailing m, SubscriptionList list) {
		if (m.getSubscriptionLists() == null) {
			m.setSubscriptionLists(new ArrayList<>(1));
			m.getSubscriptionLists().add(list);
			mRepo.save(m);
			return;
		} else {
			for (SubscriptionList l : m.getSubscriptionLists()) {
				if (l.getId().equals(list.getId()))
					return;
			}
			m.getSubscriptionLists().add(list);
			mRepo.save(m);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#removeSubscriptionList(de.mfischbo.bustamail.mailing.domain.Mailing, de.mfischbo.bustamail.mailinglist.domain.SubscriptionList)
	 */
	@Override
	public void removeSubscriptionList(Mailing m, SubscriptionList list) {
		if (m.getSubscriptionLists() == null)
			return;
		
		for (SubscriptionList l : m.getSubscriptionLists()) {
			if (l.getId().equals(list.getId())) {
				m.getSubscriptionLists().remove(l);
				mRepo.save(m);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#requestApproval(de.mfischbo.bustamail.mailing.domain.Mailing)
	 */
	@Override
	public void requestApproval(Mailing m) {
		m.setApprovalRequested(true);
		m.setDateApprovalRequested(DateTime.now());
		m.setUserApprovalRequested((User) auth.getPrincipal());
		mRepo.save(m);
	}

	@Override
	public void denyApproval(Mailing m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void approveMailing(Mailing m) {
		m.setApproved(true);
		m.setDateApproved(DateTime.now());
		m.setUserApproved((User) auth.getPrincipal());
		mRepo.save(m);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#sendPreview(de.mfischbo.bustamail.mailing.domain.Mailing)
	 */
	@Override
	public void sendPreview(Mailing m) {
		User u = (User) auth.getPrincipal();
		VersionedContent html = getRecentContent(m, ContentType.HTML);
		VersionedContent text = getRecentContent(m, ContentType.Text);

		Set<PersonalizedEmailRecipient> recipients = new HashSet<>();
		recipients.add(u);
		
		try {
			LiveMailing mailing = preProcessor.createLiveMailing(m, recipients, html.getContent(), text.getContent());
			simpleMailer.sendPreviewMailing(mailing);
		} catch (Exception ex) {
			log.error("Unable to create preview mailing. Cause: {}", ex.getMessage());
		}
	}

	
	@Override
	public boolean publishMailing(Mailing m) throws ConfigurationException {
		VersionedContent html = getRecentContent(m, ContentType.HTML);
		VersionedContent text = getRecentContent(m, ContentType.Text);
		
		// collect all subscribers
		Set<PersonalizedEmailRecipient> recipients = new HashSet<>();
		
		for (SubscriptionList l : m.getSubscriptionLists()) {
			recipients.addAll(sRepo.findAllSubscriptions(l.getId()));
		}
		
		try {
			LiveMailing liveMailing = preProcessor.createLiveMailing(m, recipients, html.getContent(), text.getContent());
			boolean success = simpleMailer.scheduleLiveMailing(liveMailing);
			if (success) {
				m.setPublished(true);
				m.setDatePublished(DateTime.now());
				m.setUserPublished((User) auth.getPrincipal());
				m.setRecipientCount(recipients.size());
				mRepo.save(m);
			}
			return success;
		} catch (Exception ex2) {
			log.error("Failed during preparation of the mailing. Cause {}", ex2.getMessage());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#checkHyperlinkConnectivity(java.util.List)
	 */
	@Override
	public List<HyperlinkDTO> checkHyperlinkConnectivity(List<HyperlinkDTO> dtos) {
		
		RestTemplate template = new RestTemplate();
		HyperlinkStatusHandler handler = new HyperlinkStatusHandler();
		template.setErrorHandler(handler);
		
		dtos.forEach(new Consumer<HyperlinkDTO>() {
			@Override
			public void accept(HyperlinkDTO t) { 
				try {
					handler.setHyperlinkDTO(t);
					template.headForHeaders(t.getTarget());
				} catch (Exception ex) {
					log.error("Error sending request to " + t.getTarget() + ". Asuming non successfull link");
					t.setSuccess(false);
					t.setHttpStatus(500);
				}
			}
		});
		return dtos;
	}
}
