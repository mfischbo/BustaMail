package de.mfischbo.bustamail.optin.service;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.optin.domain.OptinMail;
import de.mfischbo.bustamail.optin.repository.OptinMailRepo;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.service.TemplateService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;

@Service
public class OptinMailServiceImpl implements OptinMailService {

	@Inject private TemplateService tService;
	
	@Inject private OptinMailRepo oRepo;

	@Inject private VersionedContentRepository vcRepo;
	
	@Inject private Authentication auth;
	
	
	@Override
	public Page<OptinMail> getAllOptinMails(ObjectId owner, Pageable page) {
		return oRepo.findAllByOwner(owner, page);
	}

	@Override
	public OptinMail getOptinMailById(ObjectId id) {
		return oRepo.findOne(id);
	}

	@Override
	public OptinMail createOptinMail(OptinMail mail) throws EntityNotFoundException {
		User current = (User) auth.getPrincipal();
		
		mail.setDateCreated(DateTime.now());
		mail.setDateModified(mail.getDateCreated());
		mail.setUserCreated(current);
		mail.setUserModified(current);
		
		// find the template
		final ObjectId tmplId = mail.getTemplateId();
		TemplatePack tp = tService.getTemplatePackById(mail.getTemplatePack().getId());
		Template tmpl = tp.getTemplates().stream()
			.filter(t ->  t.getId().equals(tmplId))
			.findFirst()
			.get();
		
		
		if (tmpl == null) 
			throw new EntityNotFoundException("The given template could not be found");

		// crate a content version for it
		VersionedContent vc = new VersionedContent();
		vc.setContent(tmpl.getSource());
		vc.setType(ContentType.HTML);
		vc.setDateCreated(DateTime.now());
		vc.setUserCreated(current);
		
		mail = oRepo.save(mail);
		vc.setForeignId(mail.getId());
		vcRepo.save(vc);
		return mail;
	}

	@Override
	public OptinMail updateOptinMail(OptinMail mail) {
		User current = (User) auth.getPrincipal();
		
		mail.setDateModified(DateTime.now());
		mail.setUserModified(current);
		
		if (mail.isActivated()) {
			// find all currently activated an deactivate them
			List<OptinMail> active = oRepo.findAllActivated();
			active.forEach(m -> {
				if (m.isActivated() && !m.getId().equals(mail.getId())) {
					m.setActivated(false);
				}
			});
			oRepo.save(active);
		}
		return oRepo.save(mail);
	}

	@Override
	public void deleteOptinMail(OptinMail mail) {
		oRepo.delete(mail);
	}

	@Override
	public VersionedContent getCurrentContent(OptinMail mail, ContentType type) {
		PageRequest pr = new PageRequest(0, 1, Direction.DESC, "dateCreated");
		List<ContentType> t = Collections.singletonList(type);
		Page<VersionedContent> p = vcRepo.findByForeignIdAndType(mail.getId(), t, pr);
		if (p.getContent().size() > 0)
			return p.getContent().get(0);
		return null;
	}

	@Override
	public List<VersionedContent> getContents(OptinMail mail, ContentType type) {
		// TODO: Distinguish between content types here
		return vcRepo.findByForeignId(mail.getId());
	}

	@Override
	public VersionedContent createContentVersion(OptinMail mail,
			VersionedContent vc) {
		User current = (User) auth.getPrincipal();
		vc.setForeignId(mail.getId());
		vc.setDateCreated(DateTime.now());
		vc.setUserCreated(current);
		return vcRepo.save(vc);
	}
}
