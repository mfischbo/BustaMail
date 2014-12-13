package de.mfischbo.bustamail.subscriber.web;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.dto.ContactDTO;
import de.mfischbo.bustamail.subscriber.service.SubscriberService;

@RestController
@RequestMapping(value = "/api/contacts")
public class RestContactController extends BaseApiController {

	@Autowired
	private SubscriberService			service;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<ContactDTO> getAllContacts(@PageableDefault(size = 30, page = 0, sort = "lastName", direction = Direction.ASC) Pageable page) {
		return asDTO(service.getAllContacts(page), ContactDTO.class, page);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ContactDTO createContact(@RequestBody ContactDTO contact) throws Exception {
		Contact c = fromDTO(contact, Contact.class);
		return asDTO(service.createContact(c), ContactDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ContactDTO getContactById(@PathVariable("id") ObjectId id) throws Exception {
		return asDTO(service.getContactById(id), ContactDTO.class);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public ContactDTO updateContact(@PathVariable("id") ObjectId id, @RequestBody ContactDTO contact) throws Exception {
		Contact c = service.getContactById(id);
		fromDTO(contact, c);
		return asDTO(service.updateContact(c), ContactDTO.class);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteContact(@PathVariable("id") ObjectId id) throws Exception {
		Contact c = service.getContactById(id);
		service.deleteContact(c);
	}
}
