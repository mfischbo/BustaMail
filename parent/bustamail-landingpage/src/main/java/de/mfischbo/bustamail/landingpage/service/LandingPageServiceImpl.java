package de.mfischbo.bustamail.landingpage.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.HTMLPage;
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.repo.LPFormRepo;
import de.mfischbo.bustamail.landingpage.repo.LPFormSubmissionRepo;
import de.mfischbo.bustamail.landingpage.repo.LandingPageRepo;
import de.mfischbo.bustamail.landingpage.service.LandingPagePublisher.Mode;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.domain.Template;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.service.TemplateService;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;

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
	LPFormRepo			formRepo;
	
	@Inject
	LPFormSubmissionRepo	formSubRepo;

	@Inject
	Authentication		auth;

	@Inject
	VersionedContentRepository	vcRepo;

	@Inject
	Environment			env;

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#getLandingPagesByOwner(org.bson.types.ObjectId, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<LandingPage> getLandingPagesByOwner(ObjectId owner, Pageable page) {
		return lpRepo.findAllByOwner(owner, page);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#getLandingPageById(org.bson.types.ObjectId)
	 */
	@Override
	public LandingPage getLandingPageById(ObjectId id)
			throws EntityNotFoundException {
		LandingPage retval = lpRepo.findOne(id);
		checkOnNull(retval);
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#createLandingPage(de.mfischbo.bustamail.landingpage.domain.LandingPage)
	 */
	@Override
	public LandingPage createLandingPage(final LandingPage page) throws EntityNotFoundException {
		User current = (User) auth.getPrincipal();
		
		TemplatePack pack = tService.getTemplatePackById(page.getTemplatePack().getId());
		Template t = pack.getTemplates().stream()
				.filter(q -> q.getId().equals(page.getTemplateId()))
				.findFirst()
				.get();
		
		if (t == null)
			throw new EntityNotFoundException("No template found for the given id");
		
		
		OrgUnit ou = secService.getOrgUnitById(page.getOwner());
		checkOnNull(ou);
		
		DateTime now = DateTime.now();
		page.setDateCreated(now);
		page.setDateModified(now);
		page.setOwner(ou.getId());
		page.setUserCreated(current);
		page.setUserModified(current);
		page.setHtmlHeader(t.getHtmlHead());
		
		// copy all resources from the template as new media
		page.setResources(new ArrayList<Media>(t.getResources().size()));
		for (Media m : t.getResources()) {
			try {
				Media m2 = mediaService.createCopy(m, m.getName());
				page.getResources().add(m2);
			} catch (Exception ex) {
				log.warn("Unable to copy resource file : " + m.getId());
			}
		}
		LandingPage retval = lpRepo.save(page);
		
		// create a versioned content
		VersionedContent html = new VersionedContent();
		html.setContent(t.getSource());
		html.setDateCreated(retval.getDateCreated());
		html.setForeignId(retval.getId());
		html.setUserCreated(current);
		html.setType(ContentType.HTML);
		vcRepo.save(html);
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#updateLandingPage(de.mfischbo.bustamail.landingpage.domain.LandingPage)
	 */
	@Override
	public LandingPage updateLandingPage(LandingPage page)
			throws EntityNotFoundException, DataIntegrityException {
		LandingPage p = lpRepo.findOne(page.getId());
		checkOnNull(p);

		if (p.isPublished()) 
			throw new DataIntegrityException("Unable to change a published landing page");
		
		User current = (User) auth.getPrincipal();
		p.setDateModified(DateTime.now());
		p.setDescription(page.getDescription());
		p.setName(page.getName());
		p.setUserModified(current);
		p.setHtmlHeader(page.getHtmlHeader());
		return lpRepo.save(p);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#deleteLandingPage(de.mfischbo.bustamail.landingpage.domain.LandingPage)
	 */
	@Override
	public void deleteLandingPage(LandingPage page) throws DataIntegrityException {
		if (page.isPublished())
			throw new DataIntegrityException("Unable to delete a published site");
		lpRepo.delete(page);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#publishPreview(de.mfischbo.bustamail.landingpage.domain.LandingPage)
	 */
	@Override
	public void publishPreview(LandingPage page) {
		LandingPagePublisher publisher = new LandingPagePublisher(env, vcRepo, mediaService, page, Mode.PREVIEW);
		publisher.publish();
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#publishLive(de.mfischbo.bustamail.landingpage.domain.LandingPage)
	 */
	@Override
	public LandingPage publishLive(LandingPage page) {
		LandingPagePublisher publisher = new LandingPagePublisher(env, vcRepo, mediaService, page, Mode.LIVE);
		publisher.publish();
		page.setUserPublished((User) auth.getPrincipal());
		page.setDatePublished(DateTime.now());
		page.setPageUrl(publisher.getPageUrl());
		page.setPublished(true);
		return lpRepo.save(page);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#unpublishLive(de.mfischbo.bustamail.landingpage.domain.LandingPage)
	 */
	@Override
	public LandingPage unpublishLive(LandingPage page) throws BustaMailException {
		LandingPagePublisher publisher = new LandingPagePublisher(env, vcRepo, mediaService, page, Mode.LIVE);
		try {
			publisher.unpublish();
			page.setPublished(false);
			return lpRepo.save(page);
		} catch (IOException ex) {
			log.error("Caught exception when unpublishing the site. Cause: " + ex.getMessage());
			throw new BustaMailException("Unable to unpublish site. Cause was error in publisher.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#exportLandingPage(de.mfischbo.bustamail.landingpage.domain.LandingPage, java.io.OutputStream)
	 */
	@Override
	public void exportLandingPage(LandingPage page, OutputStream stream) throws BustaMailException {
		LandingPagePublisher publisher = new LandingPagePublisher(env, vcRepo, mediaService, page, Mode.EXPORT);
		try {
			publisher.publish();
			InputStream zipStream = publisher.getZippedFileStream();
			if (zipStream != null) {
				StreamUtils.copy(zipStream, stream);
				stream.flush();
				stream.close();
			}
		} catch (Exception ex) {
			log.error("Caught exception while publishing the page in mode EXPORT. Cause: " + ex.getMessage());
			throw new BustaMailException(ex.getMessage());
		}
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#getContentVersions(de.mfischbo.bustamail.landingpage.domain.HTMLPage)
	 */
	@Override
	public List<VersionedContent> getContentVersions(HTMLPage page) {
		PageRequest preq = new PageRequest(0, 20, Sort.Direction.DESC, "dateCreated");
		Page<VersionedContent> result = vcRepo.findByForeignId(page.getId(), preq);
		if (result.getTotalElements() == 0)
			return new ArrayList<VersionedContent>(0);
		else
			return result.getContent();
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#getContentVersionById(de.mfischbo.bustamail.landingpage.domain.HTMLPage, org.bson.types.ObjectId)
	 */
	@Override
	public VersionedContent getContentVersionById(HTMLPage page, ObjectId contentId) throws EntityNotFoundException {
		VersionedContent retval = vcRepo.findOne(contentId);
		checkOnNull(retval);
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#getRecentContentVersionByPage(de.mfischbo.bustamail.landingpage.domain.HTMLPage)
	 */
	@Override
	public VersionedContent getRecentContentVersionByPage(HTMLPage page) {
		PageRequest preq = new PageRequest(0, 1, Sort.Direction.DESC, "dateCreated");
		Page<VersionedContent> result = vcRepo.findByForeignId(page.getId(), preq);
		if (result.getTotalElements() == 0)
			return null;
		else return result.getContent().get(0);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#createContentVersion(de.mfischbo.bustamail.landingpage.domain.HTMLPage, de.mfischbo.bustamail.vc.domain.VersionedContent)
	 */
	@Override
	public VersionedContent createContentVersion(HTMLPage page, VersionedContent content) {
		User current = (User) auth.getPrincipal();
		
		content.setForeignId(page.getId());
		content.setDateCreated(DateTime.now());
		content.setUserCreated(current);
		content.setType(ContentType.HTML);
		return vcRepo.save(content);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#getStaticPageById(org.bson.types.ObjectId)
	 */
	@Override
	public StaticPage getStaticPageById(ObjectId id) throws EntityNotFoundException {
		LandingPage p = lpRepo.findPageContainingStaticPageById(id);
		for (StaticPage sp : p.getStaticPages())
			if (sp.getId().equals(id)) 
				return sp;
		throw new EntityNotFoundException("Unable to find static page for id : " + id);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#createStaticPage(de.mfischbo.bustamail.landingpage.domain.LandingPage, de.mfischbo.bustamail.landingpage.domain.StaticPage)
	 */
	@Override
	public StaticPage createStaticPage(final LandingPage parent, final StaticPage staticPage) throws EntityNotFoundException {
		
		User current = (User) auth.getPrincipal();
		DateTime now = DateTime.now();

		Template t = parent.getTemplatePack().getTemplates().stream()
				.filter(q -> q.getId().equals(staticPage.getTemplateId()))
				.findFirst()
				.get();

		if (t == null)
			throw new EntityNotFoundException("Unable to find template for id " + staticPage.getTemplateId());
	
		staticPage.setId(new ObjectId());
		staticPage.setOwner(parent.getOwner());
		staticPage.setDateCreated(now);
		staticPage.setDateModified(now);
		staticPage.setUserCreated(current);
		staticPage.setUserModified(current);
		staticPage.setHtmlHeader(t.getHtmlHead());
		
		if (parent.getStaticPages() == null)
			parent.setStaticPages(new ArrayList<>(1));
		parent.getStaticPages().add(staticPage);
		
		VersionedContent c = new VersionedContent();
		c.setContent(t.getSource());
		c.setDateCreated(now);
		c.setForeignId(staticPage.getId());
		c.setType(ContentType.HTML);
		c.setUserCreated(current);
		vcRepo.save(c);
		return staticPage;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#updateStaticPage(de.mfischbo.bustamail.landingpage.domain.StaticPage)
	 */
	@Override
	public StaticPage updateStaticPage(StaticPage staticPage) throws EntityNotFoundException {
		
		LandingPage p = lpRepo.findPageContainingStaticPageById(staticPage.getId());
		if (p == null)
			throw new EntityNotFoundException("Unable to find landing page for static page with id : " + staticPage.getId());
		
		staticPage.setDateModified(DateTime.now());
		staticPage.setUserModified((User) auth.getPrincipal());
		
		for (StaticPage sp : p.getStaticPages()) {
			if (sp.getId().equals(staticPage.getId())) {
				sp = staticPage;
				break;
			}
		}
		lpRepo.save(p);
		return staticPage;
	}

	@Override
	public void deleteStaticPage(StaticPage page) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#getFormById(org.bson.types.ObjectId)
	 */
	@Override
	public LPForm getFormById(ObjectId id) throws EntityNotFoundException {
		LandingPage p = lpRepo.findPageContainingFormById(id);
		for (LPForm f : p.getForms()) {
			if (f.getId().equals(id))
				return f;
		}
		throw new EntityNotFoundException("Unable to find form for id " + id);
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#createForm(de.mfischbo.bustamail.landingpage.domain.LandingPage, de.mfischbo.bustamail.landingpage.domain.LPForm)
	 */
	@Override
	public LPForm createForm(LandingPage page, LPForm form)
			throws EntityNotFoundException {

		if (page.getForms() == null)
			page.setForms(new ArrayList<>(1));
		page.getForms().add(form);
	
		lpRepo.save(page);
		return form;
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#updateForm(de.mfischbo.bustamail.landingpage.domain.LPForm)
	 */
	@Override
	public LPForm updateForm(LPForm form) throws EntityNotFoundException {
	
		LandingPage p = lpRepo.findPageContainingFormById(form.getId());
		if (p == null)
			throw new EntityNotFoundException("Unable to find form by id " + form.getId());
	
		for (LPForm f : p.getForms()) {
			if (f.getId().equals(form.getId())) {
				f = form;
				break;
			}
		}
		lpRepo.save(p);
		return form;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.landingpage.service.LandingPageService#deleteForm(de.mfischbo.bustamail.landingpage.domain.LPForm)
	 */
	@Override
	public void deleteForm(LPForm form) {

		LandingPage p = lpRepo.findPageContainingFormById(form.getId());
		if (p == null)
			return;
		
		Iterator<LPForm> fit = p.getForms().iterator();
		while (fit.hasNext()) {
			if (fit.next().getId().equals(form.getId()))
				fit.remove();
		}
		lpRepo.save(p);
	}

	@Override
	public LPFormSubmission createFormSubmission(LPFormSubmission submission) {
		submission.setDateCreated(DateTime.now());
		return formSubRepo.save(submission);
	}
}
