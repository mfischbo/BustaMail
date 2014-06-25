package de.mfischbo.bustamail.subscriber.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mfischbo.bustamail.subscriber.dto.ContactDTO;

public interface SubscriberService {

	public Page<ContactDTO> getAllContacts(Pageable page);
}
