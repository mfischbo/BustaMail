package de.mfischbo.bustamail.landingpage.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.HTMLPage;
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.dto.LPFormDTO;
import de.mfischbo.bustamail.landingpage.dto.LandingPageDTO;
import de.mfischbo.bustamail.landingpage.dto.LandingPageIndexDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageIndexDTO;
import de.mfischbo.bustamail.vc.domain.VersionedContent;

public interface LandingPageService {

	Page<LandingPage>	getLandingPagesByOwner(UUID owner, Pageable page);
	LandingPage			getLandingPageById(UUID id) throws EntityNotFoundException;

	LandingPage			createLandingPage(LandingPageIndexDTO page) throws EntityNotFoundException;
	LandingPage			updateLandingPage(LandingPageDTO page) throws EntityNotFoundException, DataIntegrityException;
	void				deleteLandingPage(LandingPage page) throws DataIntegrityException; 
	void				publishPreview(LandingPage page);
	LandingPage			publishLive(LandingPage page);
	LandingPage			unpublishLive(LandingPage page) throws BustaMailException;

	List<VersionedContent>
						getContentVersions(HTMLPage page);
	VersionedContent	getContentVersionById(HTMLPage page, UUID contentId) throws EntityNotFoundException;
	VersionedContent	getRecentContentVersionByPage(HTMLPage page);
	VersionedContent	createContentVersion(HTMLPage page, VersionedContent content);
	
	List<StaticPage>	getStaticPages(LandingPage page);
	StaticPage			getStaticPageById(UUID id) throws EntityNotFoundException;
	StaticPage			createStaticPage(LandingPage parent, StaticPageIndexDTO staticPage) throws EntityNotFoundException;
	StaticPage			updateStaticPage(StaticPageDTO staticPage) throws EntityNotFoundException;
	void				deleteStaticPage(StaticPage page);

	LPForm				getFormById(UUID id) throws EntityNotFoundException;
	LPForm				createForm(LandingPage page, LPFormDTO form) throws EntityNotFoundException;
	LPForm				updateForm(LPFormDTO form) throws EntityNotFoundException;
	void				deleteForm(LPForm form);
	
	LPFormSubmission	createFormSubmission(LPFormSubmission submission);
}