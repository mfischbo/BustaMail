package de.mfischbo.bustamail.bouncemail.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.domain.BounceMail;
import de.mfischbo.bustamail.exception.EntityNotFoundException;

@Service
public class BounceMailAccountServiceImpl implements BounceMailAccountService {

	@Override
	public Page<BounceAccount> getAllAccounts(ObjectId owner, Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BounceAccount getAccountById(ObjectId id)
			throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BounceAccount createAccount(BounceAccount account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BounceAccount updateAccout(BounceAccount account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAccount(BounceAccount account) {
		// TODO Auto-generated method stub

	}

	@Override
	public Page<BounceMail> getMailsByAccount(BounceAccount account,
			Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BounceMail getMailById(BounceAccount account, ObjectId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBounceMailsById(BounceAccount account,
			List<ObjectId> mailIds) {
		// TODO Auto-generated method stub

	}

}
