package de.mfischbo.bustamail.mailing.service;

import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.mail.internet.InternetAddress;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailer.PreviewMailing;
import de.mfischbo.bustamail.mailer.service.SimpleMailService;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.domain.VersionedContent;
import de.mfischbo.bustamail.mailing.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;
import de.mfischbo.bustamail.mailing.repository.MailingRepository;
import de.mfischbo.bustamail.mailing.repository.VersionedContentRepository;
import de.mfischbo.bustamail.mailing.repository.VersionedContentSpecification;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.service.PermissionRegistry;
import de.mfischbo.bustamail.template.util.DefaultTemplateMarkers;

@Service
public class MailingServiceImpl extends BaseService implements MailingService {

	@Autowired
	private		MailingRepository		mRepo;
	
	@Autowired
	private		VersionedContentRepository	vcRepo;
	
	@Autowired
	private		Authentication				auth;
	
	@Autowired
	private		SimpleMailService			simpleMailer;
	
	@Autowired
	private		Environment				env;
	
	
	private MailingServiceImpl() {
		MailingModulePermissionProvider mmpp = new MailingModulePermissionProvider();
		PermissionRegistry.registerPermissions(mmpp.getModulePermissions());
	}
	
	@Override
	public Page<Mailing> getAllMailings(UUID owner, Pageable page) {
		return mRepo.findAllByOwner(owner, page);
	}

	@Override
	public Mailing getMailingById(UUID id) throws EntityNotFoundException {
		Mailing m = mRepo.findOne(id);
		checkOnNull(m);
		return m;
	}

	@Override
	@Transactional
	public Mailing createMailing(Mailing m) throws EntityNotFoundException {
	
		User current = (User) auth.getPrincipal();
		m.setUserCreated(current);
		m.setUserModified(current);
		m = mRepo.saveAndFlush(m);
		
		// create a versioned content
		VersionedContent html = new VersionedContent();
		html.setContent(m.getTemplate().getSource());
		html.setDateCreated(m.getDateCreated());
		html.setMailingId(m.getId());
		html.setUserCreated(m.getUserCreated());
		html.setType(ContentType.HTML);
		html = vcRepo.saveAndFlush(html);
		
		return m;
	}

	@Override
	public Mailing updateMailing(Mailing m) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void deleteMailing(Mailing m) throws EntityNotFoundException {
		mRepo.delete(m);
		Specifications<VersionedContent> specs = Specifications.where(VersionedContentSpecification.mailingIdIs(m.getId()));
		List<VersionedContent> contents = vcRepo.findAll(specs);
		vcRepo.delete(contents);
	}
	
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
			pm.setContentProviderBaseURL(new URL(env.getProperty("de.mfischbo.bustamail.mailing.baseUrl")));
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
		Specifications<VersionedContent> specs = Specifications.where(VersionedContentSpecification.mailingIdIs(m.getId()));
		
		if (types != null && types.size() > 1) {
			specs = specs.and(VersionedContentSpecification.withAnyTypeOf(types));
		}
		return vcRepo.findAll(specs);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailing.service.MailingService#getContentById(de.mfischbo.bustamail.mailing.domain.Mailing, java.util.UUID)
	 */
	@Override
	public VersionedContent getContentById(Mailing m, UUID contentId) throws EntityNotFoundException {
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
		
		Specifications<VersionedContent> specs = Specifications.where(VersionedContentSpecification.mailingIdIs(m.getId()));
		specs = specs.and(VersionedContentSpecification.typeIs(type));
		
		PageRequest preq = new PageRequest(0, 1, Sort.Direction.DESC, "dateCreated");
		Page<VersionedContent> result = vcRepo.findAll(specs, preq);
		if (result.getTotalElements() == 0)
			return null;
		return result.getContent().get(0);
	}
	
	@Override
	public VersionedContent createContentVersion(Mailing m,
			VersionedContent c) {
		
		User current = (User) auth.getPrincipal();
		c.setMailingId(m.getId());
		c.setDateCreated(DateTime.now());
		c.setUserCreated(current);
		return vcRepo.saveAndFlush(c);
	}


	@Override
	public void requestApproval(Mailing m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void denyApproval(Mailing m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void approveMailing(Mailing m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishMailing(Mailing m) {
		// TODO Auto-generated method stub

	}

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
