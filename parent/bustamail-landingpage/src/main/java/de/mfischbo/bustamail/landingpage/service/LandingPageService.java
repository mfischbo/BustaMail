package de.mfischbo.bustamail.landingpage.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.dto.LPFormDTO;
import de.mfischbo.bustamail.landingpage.dto.LandingPageIndexDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageIndexDTO;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

public interface LandingPageService {

	Page<LandingPage>	getLandingPagesByOwner(UUID owner, Pageable page);
	LandingPage			getLandingPageById(UUID id) throws EntityNotFoundException;

	LandingPage			createLandingPage(LandingPageIndexDTO page) throws EntityNotFoundException;
	LandingPage			updateLandingPage(LandingPageIndexDTO page) throws EntityNotFoundException;
	void				deleteLandingPage(LandingPage page); 
	
	VersionedContent	getRecentContentVersionByPage(LandingPage page);
	VersionedContent	createContentVersion(LandingPage page, VersionedContent content);
	
	List<StaticPage>	getStaticPages(LandingPage page);
	StaticPage			getStaticPageById(UUID id) throws EntityNotFoundException;
	StaticPage			createStaticPage(LandingPage parent, StaticPageIndexDTO staticPage) throws EntityNotFoundException;
	StaticPage			updateStaticPage(StaticPageIndexDTO staticPage) throws EntityNotFoundException;
	void				deleteStaticPage(StaticPage page);
	
	VersionedContent	getRecentContentVersionByPage(StaticPage page);
	VersionedContent	createContentVersion(StaticPage page, VersionedContentDTO content);
	

	LPForm				getFormById(UUID id) throws EntityNotFoundException;
	LPForm				createForm(LandingPage page, LPFormDTO form) throws EntityNotFoundException;
	LPForm				updateForm(LPFormDTO form) throws EntityNotFoundException;
	void				deleteForm(LPForm form);
}
