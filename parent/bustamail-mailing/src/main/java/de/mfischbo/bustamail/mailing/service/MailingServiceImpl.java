package de.mfischbo.bustamail.mailing.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.ConfigurationException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.dto.PreviewMailing;
import de.mfischbo.bustamail.mailer.service.SimpleMailService;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;
import de.mfischbo.bustamail.mailing.repository.MailingRepository;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.util.DefaultTemplateMarkers;
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

	private static final String 	BASE_URL_KEY = "de.mfischbo.bustamail.mailing.baseUrl";
	
	@Autowired
	private		MailingRepository		mRepo;
	
	@Autowired
	private		VersionedContentRepository	vcRepo;
	
	@Autowired
	private		SubscriptionRepository		sRepo;
	
	@Autowired
	private		Authentication				auth;
	
	@Autowired
	private		SimpleMailService			simpleMailer;
	
	@Autowired
	private		Environment				env;
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#getAllMailings(java.util.UUID, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<Mailing> getAllMailings(ObjectId owner, Pageable page) {
		return mRepo.findAllByOwner(owner, page);
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
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#sendPreview(de.mfischbo.bustamail.mailing.domain.Mailing)
	 */
	@Override
	public void sendPreview(Mailing m) {
		User u = (User) auth.getPrincipal();
		VersionedContent c = getRecentContent(m, ContentType.HTML);
	
		PreviewMailing pm = new PreviewMailing(m.getSubject(), c.getContent(), null, u);
		for (String s : DefaultTemplateMarkers.getTemplateClassMarkers())
			pm.addRemoveClass(s);
		
		for (String s : DefaultTemplateMarkers.getTemplateAttributeMarkers())
			pm.addRemoveAttribute(s);
	
		pm.setDisableLinkTrackClass(DefaultTemplateMarkers.getDiableLinkTrackClass());
		
		try {
			pm.setContentProviderBaseURL(new URL(env.getProperty(BASE_URL_KEY)));
		} catch (Exception ex) {
			log.error("The provided base url is not valid! Check the configuration property de.mfischbo.bustamail.mailing.baseUrl!");
		}
		
		try {
			InternetAddress sender = new InternetAddress(m.getSenderAddress());
			pm.setSenderAddress(sender);
		} catch (Exception ex) {
			log.error("Unable to set the senders address. Provided Address was: " + m.getSenderAddress());
		}
	
		pm.setSenderName(m.getSenderName());
		
		if (pm.isValid())
			simpleMailer.sendPreviewMailing(pm);
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

	@Override
	public boolean publishMailing(Mailing m) throws ConfigurationException {
		VersionedContent html = getRecentContent(m, ContentType.HTML);
		VersionedContent text = getRecentContent(m, ContentType.Text);
		String baseUrl 		  = env.getProperty(BASE_URL_KEY);
		
		// collect all subscribers
		List<Subscription> subscriptions = new LinkedList<>();
		for (SubscriptionList l : m.getSubscriptionLists()) {
			subscriptions.addAll(sRepo.findAllSubscriptions(l.getId()));
		}
		
		try {
			LiveMailing liveMailing = MailingPreProcessor.createLiveMailing(m, subscriptions, html, text, baseUrl);
			boolean success = simpleMailer.scheduleLiveMailing(liveMailing);
			if (success) {
				m.setPublished(true);
				m.setDatePublished(DateTime.now());
				m.setUserPublished((User) auth.getPrincipal());
				mRepo.save(m);
			}
			return success;
		} catch (MalformedURLException ex) {
			throw new ConfigurationException("The value provided : " + baseUrl + " is not a valid URL");
		} catch (AddressException ex2) {
			log.error("Sender address is not a valid InternetAddress. Got: " + ex2.getMessage());
			// this should not occur, since we're checking the addresses when setting it on the mailing
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
