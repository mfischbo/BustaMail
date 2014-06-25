package de.mfischbo.bustamail.mailing.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.domain.VersionedContent;
import de.mfischbo.bustamail.mailing.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.mailing.dto.HyperlinkDTO;

public interface MailingService {

	@PreAuthorize("hasPermission(#owner, 'Mailings.USE_MAILINGS')")
	Page<Mailing>		getAllMailings(UUID owner, Pageable page);
	
	@PostAuthorize("hasPermission(returnObject.owner, 'Mailings.USE_MAILINGS')")
	Mailing				getMailingById(UUID id) throws EntityNotFoundException;

	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	Mailing				createMailing(@P("m") Mailing m) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	Mailing				updateMailing(@P("m") Mailing m) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				deleteMailing(@P("m") Mailing m) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				sendPreview(@P("m") Mailing m);
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	List<VersionedContent>
						getContentVersions(@P("m") Mailing m, List<ContentType> types);
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.EDIT_CONTENTS')")
	VersionedContent	getContentById(@P("m") Mailing m, UUID contentId) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.USE_MAILINGS')")
	VersionedContent	getRecentContent(@P("m") Mailing m, ContentType type);
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.EDIT_CONTENTS')")
	VersionedContent	createContentVersion(@P("m") Mailing m, VersionedContent c);
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.MANAGE_MAILINGS')")
	void				requestApproval(@P("m") Mailing m);
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.APPROVE_MAILINGS')")
	void				denyApproval(@P("m") Mailing m);
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.APPROVE_MAILINGS')")
	void				approveMailing(@P("m") Mailing m);
	
	@PreAuthorize("hasPermission(#m.owner, 'Mailings.PUBLISH_MAILINGS')")
	void				publishMailing(@P("m") Mailing m);
	
	List<HyperlinkDTO>  checkHyperlinkConnectivity(List<HyperlinkDTO> dtos);
}
