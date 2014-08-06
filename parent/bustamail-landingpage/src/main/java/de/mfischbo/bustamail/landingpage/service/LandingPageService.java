package de.mfischbo.bustamail.landingpage.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.landingpage.domain.StaticPage;
import de.mfischbo.bustamail.landingpage.dto.LandingPageIndexDTO;
import de.mfischbo.bustamail.landingpage.dto.StaticPageIndexDTO;
import de.mfischbo.bustamail.vc.domain.VersionedContent;

public interface LandingPageService {

	Page<LandingPage>	getLandingPagesByOwner(UUID owner, Pageable page);
	LandingPage			getLandingPageById(UUID id) throws EntityNotFoundException;

	LandingPage			createLandingPage(LandingPageIndexDTO page) throws EntityNotFoundException;
	LandingPage			updateLandingPage(LandingPageIndexDTO page) throws EntityNotFoundException;
	void				deleteLandingPage(LandingPage page); 
	
	VersionedContent	getRecentContentVersionByPage(LandingPage page);
	
	
	List<StaticPage>	getStaticPages(LandingPage page);
	StaticPage			getStaticPageById(UUID id) throws EntityNotFoundException;
	StaticPage			createStaticPage(StaticPageIndexDTO staticPage);
	StaticPage			updateStaticPage(StaticPageIndexDTO staticPage);
	void				deleteStaticPage(StaticPage page);
	VersionedContent	getRecentContentVersionByPage(StaticPage page);
}
