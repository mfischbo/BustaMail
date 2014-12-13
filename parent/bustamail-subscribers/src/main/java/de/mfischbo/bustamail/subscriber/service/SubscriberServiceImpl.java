package de.mfischbo.bustamail.subscriber.service;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.repository.ContactRepository;

@Service
public class SubscriberServiceImpl extends BaseService implements SubscriberService {

	@Inject
	private ContactRepository			cntRepo;
	

	@Override
	public Page<Contact> getAllContacts(Pageable page) {
		Page<Contact> retval = cntRepo.findAll(page);
		return retval;
	}
	
	@Override
	public Contact getContactById(ObjectId id) throws EntityNotFoundException {
		Contact retval = cntRepo.findOne(id);
		if (retval == null)
			throw new EntityNotFoundException("Unable to find contact for id " + id);
		return retval;
	}

	@Override
	public Contact getContactByEMailAddress(EMailAddress e) throws EntityNotFoundException {
		return cntRepo.findByEmailAddress(e.toString());
	}

	@Override
	public Contact createContact(Contact c) {
		return cntRepo.save(c);
	}

	@Override
	public Contact updateContact(Contact c) throws EntityNotFoundException {
		Contact cnt = cntRepo.findOne(c.getId());
		if (cnt == null) 
			throw new EntityNotFoundException("Unable to find contact for id : " + c.getId());
		super.fromDTO(c, cnt);
		return cntRepo.save(cnt);
	}
	
	@Override
	public void deleteContact(Contact c) {
		cntRepo.delete(c);
	}
}