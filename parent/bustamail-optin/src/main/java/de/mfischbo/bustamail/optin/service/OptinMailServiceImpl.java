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

import de.mfischbo.bustamail.optin.domain.OptinMail;
import de.mfischbo.bustamail.optin.repository.OptinMailRepo;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;

@Service
public class OptinMailServiceImpl implements OptinMailService {

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
	public OptinMail createOptinMail(OptinMail mail) {
		User current = (User) auth.getPrincipal();
		
		mail.setDateCreated(DateTime.now());
		mail.setDateModified(mail.getDateCreated());
		mail.setUserCreated(current);
		mail.setUserModified(current);
		return oRepo.save(mail);
	}

	@Override
	public OptinMail updateOptinMail(OptinMail mail) {
		User current = (User) auth.getPrincipal();
		
		mail.setDateModified(DateTime.now());
		mail.setUserModified(current);
		return oRepo.save(mail);
	}

	@Override
	public void deleteOptinMail(OptinMail mail) {
		oRepo.delete(mail);
	}

	@Override
	public VersionedContent getCurrentContent(OptinMail mail, ContentType type) {
		PageRequest pr = new PageRequest(1, 1, Direction.DESC, "dateCreated");
		List<ContentType> t = Collections.singletonList(type);
		Page<VersionedContent> p = vcRepo.findByForeignIdAndType(mail.getId(), t, pr);
		if (p.getContent().size() > 0)
			return p.getContent().get(0);
		return null;
	}

	@Override
	public Page<VersionedContent> getContents(OptinMail mail, ContentType type,
			Pageable page) {
		List<ContentType> t = Collections.singletonList(type);
		return vcRepo.findByForeignIdAndType(mail.getId(), t, page);
	}

}
