package de.mfischbo.bustamail.subscriber.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.subscriber.dto.ContactDTO;
import de.mfischbo.bustamail.subscriber.service.SubscriberService;

@RestController
@RequestMapping(value = "/api/contacts")
public class RestContactController extends BaseApiController {

	@Autowired
	private SubscriberService			service;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<ContactDTO> getAllContacts(@PageableDefault(size = 30, page = 0, sort = "lastName", direction = Direction.ASC) Pageable page) {
		return service.getAllContacts(page);
	}
	
}
