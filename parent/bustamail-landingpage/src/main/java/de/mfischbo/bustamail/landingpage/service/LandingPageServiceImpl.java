package de.mfischbo.bustamail.landingpage.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.dto.LandingPageIndexDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageIndexDTO;
import de.mfischbo.bustamail.landingpage.repo.LandingPageRepo;
import de.mfischbo.bustamail.landingpage.repo.StaticPageRepo;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.service.TemplateService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;
import de.mfischbo.bustamail.vc.repo.VersionedContentSpecification;

@Service
public class LandingPageServiceImpl extends BaseService implements LandingPageService {

	@Inject
	TemplateService		tService;
	
	@Inject
	SecurityService		secService;
	
	@Inject
	LandingPageRepo		lpRepo;
	
	@Inject
	StaticPageRepo		spRepo;

	@Inject
	Authentication		auth;

	@Inject
	VersionedContentRepository	vcRepo;
	
	@Override
	public Page<LandingPage> getLandingPagesByOwner(UUID owner, Pageable page) {
		return lpRepo.findAllByOwner(owner, page);
	}

	@Override
	public LandingPage getLandingPageById(UUID id)
			throws EntityNotFoundException {
		LandingPage retval = lpRepo.findOne(id);
		checkOnNull(retval);
		return retval;
	}

	@Override
	public LandingPage createLandingPage(LandingPageIndexDTO page) throws EntityNotFoundException {
		LandingPage p = new LandingPage();
		User current = (User) auth.getPrincipal();
		Template t = tService.getTemplateById(page.getTemplate().getId());
		checkOnNull(t);
		
		OrgUnit ou = secService.getOrgUnitById(page.getOwner());
		checkOnNull(ou);
		
		DateTime now = DateTime.now();
		p.setDateCreated(now);
		p.setDateModified(now);
		p.setDescription(page.getDescription());
		p.setName(page.getName());
		p.setOwner(ou.getId());
		p.setTemplate(t);
		p.setUserCreated(current);
		p.setUserModified(current);
		p = lpRepo.saveAndFlush(p);
		
		// create a versioned content
		VersionedContent html = new VersionedContent();
		html.setContent(t.getSource());
		html.setDateCreated(p.getDateCreated());
		html.setMailingId(p.getId());
		html.setUserCreated(current);
		html.setType(ContentType.HTML);
		vcRepo.saveAndFlush(html);
	
		return p;
	}

	@Override
	public LandingPage updateLandingPage(LandingPageIndexDTO page)
			throws EntityNotFoundException {
		LandingPage p = lpRepo.findOne(page.getId());
		checkOnNull(p);
	
		User current = (User) auth.getPrincipal();
		Template t = tService.getTemplateById(page.getTemplate().getId());
		checkOnNull(t);
		
		p.setDateModified(DateTime.now());
		p.setDescription(page.getDescription());
		p.setName(p.getName());
		p.setTemplate(t);
		p.setUserModified(current);
		return lpRepo.saveAndFlush(p);
	}

	@Override
	public void deleteLandingPage(LandingPage page) {
		lpRepo.delete(page);
	}

	@Override
	public VersionedContent getRecentContentVersionByPage(LandingPage page) {
		Specifications<VersionedContent> specs = Specifications.where(VersionedContentSpecification.mailingIdIs(page.getId()));
		PageRequest preq = new PageRequest(0, 1, Sort.Direction.DESC, "dateCreated");
		Page<VersionedContent> result = vcRepo.findAll(specs, preq);
		if (result.getTotalElements() == 0)
			return null;
		else return result.getContent().get(0);
	}
	
	
	@Override
	public VersionedContent createContentVersion(LandingPage page, VersionedContent content) {
		User current = (User) auth.getPrincipal();
		
		content.setMailingId(page.getId());
		content.setDateCreated(DateTime.now());
		content.setUserCreated(current);
		content.setType(ContentType.HTML);
		return vcRepo.saveAndFlush(content);
	}

	@Override
	public List<StaticPage> getStaticPages(LandingPage page) {
		return null;
	}

	@Override
	public StaticPage getStaticPageById(UUID id) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaticPage createStaticPage(StaticPageIndexDTO staticPage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaticPage updateStaticPage(StaticPageIndexDTO staticPage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteStaticPage(StaticPage page) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public VersionedContent getRecentContentVersionByPage(StaticPage page) {
		// TODO Auto-generated method stub
		return null;
	}

}
