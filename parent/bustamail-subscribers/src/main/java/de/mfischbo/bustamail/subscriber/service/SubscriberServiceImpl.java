package de.mfischbo.bustamail.subscriber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.dto.ContactDTO;
import de.mfischbo.bustamail.subscriber.repository.ContactRepository;

@Service
public class SubscriberServiceImpl extends BaseService implements SubscriberService {

	@Autowired
	private ContactRepository			cntRepo;
	
	@Override
	public Page<ContactDTO> getAllContacts(Pageable page) {
		Page<Contact> retval = cntRepo.findAll(page);
		return asDTO(retval, ContactDTO.class, page);
	}
}
