package de.mfischbo.bustamail.subscriber.service;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.repository.ContactRepository;
import de.mfischbo.bustamail.subscriber.repository.EMailAddressRepository;
import de.mfischbo.bustamail.subscriber.repository.EMailAddressSpecification;

@Service
public class SubscriberServiceImpl extends BaseService implements SubscriberService {

	@Inject
	private ContactRepository			cntRepo;
	
	@Inject
	private EMailAddressRepository		emailRepo;
	
	@Override
	public Page<Contact> getAllContacts(Pageable page) {
		Page<Contact> retval = cntRepo.findAll(page);
		return retval;
	}
	
	@Override
	public Contact getContactById(UUID id) throws EntityNotFoundException {
		Contact retval = cntRepo.findOne(id);
		if (retval == null)
			throw new EntityNotFoundException("Unable to find contact for id " + id);
		return retval;
	}

	@Override
	public Contact getContactByEMailAddress(EMailAddress e) throws EntityNotFoundException {
		Specifications<EMailAddress> specs = Specifications.where(EMailAddressSpecification.isEqualTo(e));
		EMailAddress address = emailRepo.findOne(specs);
		if (address == null)
			throw new EntityNotFoundException("Unable to find contact with email address : " + e.toString());
		return address.getContact();
	}

	@Override
	public Contact createContact(Contact c) {
		return cntRepo.saveAndFlush(c);
	}

	@Override
	public Contact updateContact(Contact c) throws EntityNotFoundException {
		Contact cnt = cntRepo.findOne(c.getId());
		if (cnt == null) 
			throw new EntityNotFoundException("Unable to find contact for id : " + c.getId());
		super.fromDTO(c, cnt);
		return cntRepo.saveAndFlush(cnt);
	}
	
	@Override
	public void deleteContact(Contact c) {
		cntRepo.delete(c);
	}
}