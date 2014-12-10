package de.mfischbo.bustamail.subscriber.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public interface SubscriberService {

	public Page<Contact> getAllContacts(Pageable page);

	public Contact getContactById(UUID id) throws EntityNotFoundException;
	
	public Contact getContactByEMailAddress(EMailAddress e) throws EntityNotFoundException;

	@Transactional
	public Contact createContact(Contact c);
	
	@Transactional
	public Contact updateContact(Contact c) throws EntityNotFoundException;
	
	public void deleteContact(Contact c);
}
