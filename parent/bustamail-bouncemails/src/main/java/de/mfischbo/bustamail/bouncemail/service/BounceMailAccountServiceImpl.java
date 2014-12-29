package de.mfischbo.bustamail.bouncemail.service;

import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.domain.BounceMail;
import de.mfischbo.bustamail.bouncemail.repo.BounceAccountRepo;
import de.mfischbo.bustamail.bouncemail.repo.BounceMailRepo;
import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.security.domain.User;

@Service
public class BounceMailAccountServiceImpl extends BaseService implements BounceMailAccountService {

	@Inject
	private BounceAccountRepo		baRepo;
	
	@Inject
	private BounceMailRepo			bmRepo;
	
	@Inject
	private Authentication			auth;
	
	@Override
	public Page<BounceAccount> getAllAccounts(ObjectId owner, Pageable page) {
		return baRepo.findAllByOwner(owner, page);
	}

	@Override
	public BounceAccount getAccountById(ObjectId id)
			throws EntityNotFoundException {
		BounceAccount account =  baRepo.findOne(id);
		checkOnNull(account);
		return account;
	}

	@Override
	public BounceAccount createAccount(BounceAccount account) {
		DateTime now = DateTime.now();
		account.setDateCreated(now);
		account.setDateModified(now);
		
		User current = (User) auth.getPrincipal();
		account.setUserCreated(current);
		account.setUserModified(current);
		
		return baRepo.save(account);
	}

	@Override
	public BounceAccount updateAccout(BounceAccount account) {
		account.setDateModified(DateTime.now());
		account.setUserModified((User) auth.getPrincipal());
		return baRepo.save(account);
	}

	@Override
	public void deleteAccount(BounceAccount account) {
		baRepo.delete(account);
	}

	@Override
	public Page<BounceMail> getMailsByAccount(BounceAccount account,
			Pageable page) {
		return bmRepo.findAllByAccount(account, page);
	}

	@Override
	public BounceMail getMailById(BounceAccount account, ObjectId id) throws EntityNotFoundException {
		BounceMail mail = bmRepo.findOneByAccount(account, id);
		checkOnNull(mail);
		return mail;
	}

	@Override
	public void deleteBounceMailsById(BounceAccount account,
			List<ObjectId> mailIds) {
		Iterable<BounceMail> it = bmRepo.findAll(mailIds);
		bmRepo.delete(it);
	}
}