package de.mfischbo.bustamail.subscriber.service;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public interface SubscriberService {

	public Page<Contact> getAllContacts(Pageable page);

	public Contact getContactById(ObjectId id) throws EntityNotFoundException;
	
	public Contact getContactByEMailAddress(EMailAddress e) throws EntityNotFoundException;

	public Contact createContact(Contact c);
	
	public Contact updateContact(Contact c) throws EntityNotFoundException;
	
	public void deleteContact(Contact c);
}
