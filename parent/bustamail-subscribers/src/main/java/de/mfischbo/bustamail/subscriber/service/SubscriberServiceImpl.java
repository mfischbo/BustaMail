package de.mfischbo.bustamail.subscriber.service;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.dto.ContactDTO;
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
	public Page<ContactDTO> getAllContacts(Pageable page) {
		Page<Contact> retval = cntRepo.findAll(page);
		return asDTO(retval, ContactDTO.class, page);
	}

	@Override
	public Contact getContactByEMailAddress(EMailAddress e) {
		Specifications<EMailAddress> specs = Specifications.where(EMailAddressSpecification.isEqualTo(e));
		EMailAddress address = emailRepo.findOne(specs);
		if (address == null)
			return null;
		return address.getContact();
	}

	@Override
	public Contact createContact(Contact c) {
		return cntRepo.saveAndFlush(c);
	}

	@Override
	public Contact updateContact(Contact c) {
		return cntRepo.saveAndFlush(c);
	}
}
