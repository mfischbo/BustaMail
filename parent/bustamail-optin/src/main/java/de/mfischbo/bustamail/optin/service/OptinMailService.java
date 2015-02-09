package de.mfischbo.bustamail.optin.service;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.optin.domain.OptinMail;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;

public interface OptinMailService {

	@PreAuthorize("hasPermission(#owner, 'OptinMails.USE_OPTIN_MAILS')")
	public Page<OptinMail> getAllOptinMails(ObjectId owner, Pageable page);
	
	@PostAuthorize("hasPermission(returnObject.owner, 'OptinMails.USE_OPTIN_MAILS')")
	public OptinMail		getOptinMailById(ObjectId id);
	
	@PreAuthorize("hasPermission(#mail.owner, 'OptinMails.MANAGE_OPTIN_MAILS')")
	public OptinMail		createOptinMail(OptinMail mail);
	
	@PreAuthorize("hasPermission(#mail.owner, 'OptinMails.MANAGE_OPTIN_MAILS')")
	public OptinMail		updateOptinMail(OptinMail mail);
	
	@PreAuthorize("hasPermission(#mail.owner, 'OptinMails.MANAGE_OPTIN_MAILS')")
	public void				deleteOptinMail(OptinMail mail);
	
	public VersionedContent getCurrentContent(OptinMail mail, ContentType type);
	
	public Page<VersionedContent> getContents(OptinMail mail, ContentType type, Pageable page);
}
