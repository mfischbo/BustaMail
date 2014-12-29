package de.mfischbo.bustamail.bouncemail.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.bouncemail.domain.BounceMail;
import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.exception.EntityNotFoundException;

public interface BounceMailAccountService {

	@PreAuthorize("hasPermission(#owner, 'BounceAccounts.USE_ACCOUNTS')")
	public Page<BounceAccount> getAllAccounts(ObjectId owner, Pageable page);
	
	@PostAuthorize("hasPermission(returnObject.owner, 'BounceAccounts.USE_ACCOUNTS')")
	public BounceAccount getAccountById(ObjectId id) throws EntityNotFoundException;

	@PreAuthorize("hasPermission(account.owner, 'BounceAccounts.MANAGE_ACCOUNTS')")
	public BounceAccount createAccount(BounceAccount account);
	
	@PreAuthorize("hasPermission(account.owner, 'BounceAccounts.MANAGE_ACCOUNTS')")
	public BounceAccount updateAccout(BounceAccount account);
	
	@PreAuthorize("hasPermission(account.owner, 'BounceAccounts.MANAGE_ACCOUNTS')")
	public void deleteAccount(BounceAccount account);
	
	@PreAuthorize("hasPermission(account.owner, 'BounceAccounts.READ_MAILS')")
	public Page<BounceMail> getMailsByAccount(BounceAccount account, Pageable page);
	
	@PreAuthorize("hasPermission(account.owner, 'BounceAccounts.READ_MAILS')")
	public BounceMail getMailById(BounceAccount account, ObjectId id);
	
	@PreAuthorize("hasPermission(account.owner, 'BounceAccounts.MANAGE_MAILS'")
	public void deleteBounceMailsById(BounceAccount account, List<ObjectId> mailIds);
}
