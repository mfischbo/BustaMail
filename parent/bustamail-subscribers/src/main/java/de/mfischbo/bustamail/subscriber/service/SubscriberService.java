package de.mfischbo.bustamail.subscriber.service;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.dto.ContactDTO;

public interface SubscriberService {

	public Page<ContactDTO> getAllContacts(Pageable page);
	
	public Contact getContactByEMailAddress(EMailAddress e);

	@Transactional
	public Contact createContact(Contact c);
	
	@Transactional
	public Contact updateContact(Contact c);
}
