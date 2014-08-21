package de.mfischbo.bustamail.landingpage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormEntry;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.dto.LPFormDTO;
import de.mfischbo.bustamail.landingpage.dto.LPFormEntryDTO;
import de.mfischbo.bustamail.landingpage.dto.LandingPageIndexDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageIndexDTO;
import de.mfischbo.bustamail.landingpage.repo.LPFormRepo;
import de.mfischbo.bustamail.landingpage.repo.LandingPageRepo;
import de.mfischbo.bustamail.landingpage.repo.StaticPageRepo;
import de.mfischbo.bustamail.landingpage.service.LandingPagePublisher.Mode;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.service.TemplateService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;
import de.mfischbo.bustamail.vc.repo.VersionedContentSpecification;

@Service
public class LandingPageServiceImpl extends BaseService implements LandingPageService {

	@Inject
	TemplateService		tService;
	
	@Inject
	SecurityService		secService;
	
	@Inject
	MediaService		mediaService;
	
	@Inject
	LandingPageRepo		lpRepo;
	
	@Inject
	StaticPageRepo		spRepo;
	
	@Inject
	LPFormRepo			formRepo;

	@Inject
	Authentication		auth;

	@Inject
	VersionedContentRepository	vcRepo;

	@Inject
	Environment			env;
	
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
		p.setDateModified(DateTime.now());
		p.setDescription(page.getDescription());
		p.setName(page.getName());
		p.setUserModified(current);
		return lpRepo.saveAndFlush(p);
	}

	@Override
	public void deleteLandingPage(LandingPage page) {
		lpRepo.delete(page);
	}
	
	@Override
	public void publishPreview(LandingPage page) {
		LandingPagePublisher publisher = new LandingPagePublisher(env, vcRepo, mediaService, page, Mode.PREVIEW);
		publisher.publish();
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
		StaticPage p = spRepo.findOne(id);
		checkOnNull(p);
		return p;
	}

	@Override
	public StaticPage createStaticPage(LandingPage parent, StaticPageIndexDTO staticPage) throws EntityNotFoundException {
		
		User current = (User) auth.getPrincipal();
		DateTime now = DateTime.now();
		
		Template t = tService.getTemplateById(staticPage.getTemplate().getId());
		
		StaticPage p = new StaticPage();
		p.setDescription(staticPage.getDescription());
		p.setName(staticPage.getName());
		p.setDateCreated(now);
		p.setDateModified(now);
		p.setUserCreated(current);
		p.setUserModified(current);
		p.setTemplate(t);
		p.setParent(parent);
		p = spRepo.saveAndFlush(p);
		
		VersionedContent c = new VersionedContent();
		c.setContent(t.getSource());
		c.setDateCreated(now);
		c.setMailingId(p.getId());
		c.setType(ContentType.HTML);
		c.setUserCreated(current);
		vcRepo.saveAndFlush(c);
		return p;
	}

	@Override
	public StaticPage updateStaticPage(StaticPageIndexDTO staticPage) throws EntityNotFoundException {
		StaticPage p = spRepo.findOne(staticPage.getId());
		checkOnNull(p);
		
		p.setName(staticPage.getName());
		p.setDescription(staticPage.getDescription());
		p.setDateModified(DateTime.now());
		p.setUserModified((User) auth.getPrincipal());
		return spRepo.saveAndFlush(p);
	}

	@Override
	public void deleteStaticPage(StaticPage page) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public VersionedContent getRecentContentVersionByPage(StaticPage page) {
		Specifications<VersionedContent> specs = Specifications.where(VersionedContentSpecification.mailingIdIs(page.getId()));
		PageRequest preq  = new PageRequest(0, 1, Sort.Direction.DESC, "dateCreated");
		Page<VersionedContent> result = vcRepo.findAll(specs, preq);
		if (result.getTotalElements() == 0)
			return null;
		else return result.getContent().get(0);
	}

	@Override
	public VersionedContent createContentVersion(StaticPage page,
			VersionedContentDTO content) {
	
		VersionedContent c = new VersionedContent();
		c.setContent(content.getContent());
		c.setDateCreated(DateTime.now());
		c.setMailingId(page.getId());
		c.setType(ContentType.HTML);
		c.setUserCreated((User) auth.getPrincipal());
		return vcRepo.saveAndFlush(c);
	}


	@Override
	public LPForm getFormById(UUID id) throws EntityNotFoundException {
		LPForm form = formRepo.findOne(id);
		checkOnNull(form);
		return form;
	}

	@Override
	public LPForm createForm(LandingPage page, LPFormDTO form)
			throws EntityNotFoundException {
	
		LPForm f = new LPForm();
		f.setConversion(form.isConversion());
		f.setName(form.getName());
		f.setOnSuccessAction(form.getOnSuccessAction());
		f.setRedirectTarget(form.getRedirectTarget());
		f.setLandingPage(page);
		f.setFields(new ArrayList<LPFormEntry>(form.getFields().size()));
		for (LPFormEntryDTO d : form.getFields()) {
			
			LPFormEntry e = new LPFormEntry();
			e.setName(d.getName());
			e.setRegexp(d.getRegexp());
			e.setRequired(d.isRequired());
			e.setValidationType(d.getValidationType());
			f.getFields().add(e);
		}
		return formRepo.saveAndFlush(f);
	}

	@Override
	public LPForm updateForm(LPFormDTO form) throws EntityNotFoundException {
		
		LPForm f = formRepo.findOne(form.getId());
		checkOnNull(f);
		
		f.setConversion(form.isConversion());
		f.setName(form.getName());
		f.setOnSuccessAction(form.getOnSuccessAction());
		f.setRedirectTarget(form.getRedirectTarget());
		List<LPFormEntry> fields = new ArrayList<>(form.getFields().size());
		for (LPFormEntryDTO d : form.getFields()) {
			
			LPFormEntry e = new LPFormEntry();
			e.setName(d.getName());
			e.setRegexp(d.getRegexp());
			e.setRequired(d.isRequired());
			e.setValidationType(d.getValidationType());
			fields.add(e);
		}
		f.setFields(fields);
		return formRepo.saveAndFlush(f);
	}

	@Override
	public void deleteForm(LPForm form) {
		formRepo.delete(form);
	}
}
